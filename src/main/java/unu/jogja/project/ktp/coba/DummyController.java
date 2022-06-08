/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package unu.jogja.project.ktp.coba;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import unu.jogja.project.ktp.coba.exceptions.NonexistentEntityException;

/**
 *
 * @author harry
 */
@Controller
public class DummyController {
    
    DummyJpaController dummyctrl = new DummyJpaController();
    List<Dummy> data = new ArrayList<>();
    @RequestMapping("/dummy")
//    @ResponseBody
    public String getDummy(Model model){
    
        int record = dummyctrl.getDummyCount();
        String result = "";
        try{
            data = dummyctrl.findDummyEntities().subList(0, record);
        }
        catch (Exception e){
            result=e.getMessage();
        }
        
        model.addAttribute("goDummy", data);
        model.addAttribute("record", record);
         
        return "dummy";
    }
    
    @RequestMapping("/create")
    public String createDummy(){
        return "dummy/create";
    }
    
    @PostMapping(value = "/newdata", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public String newDummy(HttpServletRequest data,@RequestParam("gambar") MultipartFile file, RedirectAttributes ra) throws ParseException, Exception{
        Dummy dumdata = new Dummy();
        
        String id = data.getParameter("id");
        int iid = Integer.parseInt(id);
        
        String tanggal = data.getParameter("date");
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tanggal);
        
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        byte[] image = file.getBytes();
        
        dumdata.setId(iid);
        dumdata.setTanggal(date);
        dumdata.setGambar(image);
        
        dummyctrl.create(dumdata);
        ra.addFlashAttribute("sukses", "Sukses menambahkan data!");
        return "redirect:/dummy";
    }
    
    @RequestMapping(value = "/gambar/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getGambar(@PathVariable("id") Integer gambarId) throws IOException {
        byte[] imageContent = dummyctrl.findDummy(gambarId).getGambar();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<byte[]>(imageContent, headers, HttpStatus.OK);
    }

    @GetMapping("/delete/{id}")
    public String deleteDummy(@PathVariable("id") Integer id, RedirectAttributes ra) throws NonexistentEntityException{
        dummyctrl.destroy(id);
        ra.addFlashAttribute("sukses", "Sukses menghapus data!");
        return "redirect:/dummy";
    }
    
    
    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Dummy dummy = dummyctrl.findDummy(id);
        model.addAttribute("dummy", dummy);
        return "dummy/update";
    }
    
    @PostMapping(value="/saveupdate", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public String saveDummy(HttpServletRequest data, @RequestParam("gambar") MultipartFile file, RedirectAttributes ra) throws ParseException, IOException, Exception{
        Dummy dumdata = new Dummy();
        String id11 = data.getParameter("idupdate1");
        Integer i= Integer.parseInt(id11);
        dumdata = dummyctrl.findDummy(i);
        
        String id1 = data.getParameter("idupdate1");
        int iid = Integer.parseInt(id1);
        
        String tanggal = data.getParameter("date");
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tanggal);
        
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        
        if(!filename.isEmpty()){
            byte[] image = file.getBytes();
        
            dumdata.setId(iid);
            dumdata.setTanggal(date);
            dumdata.setGambar(image);

            dummyctrl.edit(dumdata);
            ra.addFlashAttribute("sukses", "Sukses mengupdate data!");
            return "redirect:/dummy";
        }else{        
            dumdata.setId(iid);
            dumdata.setTanggal(date);

            dummyctrl.edit(dumdata);
            ra.addFlashAttribute("sukses", "Sukses mengupdate data!");
            return "redirect:/dummy";
        }
    }
}