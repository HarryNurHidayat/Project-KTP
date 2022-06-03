/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package unu.jogja.project.ktp;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author harry
 */

@Controller
public class DataController {
    
    DataJpaController dataktpJpaController = new DataJpaController();
    List<Data> newdata = new ArrayList<>();
    
    @RequestMapping("/main")
    public String getMain(){
        return "menu";
    }
    
    @RequestMapping("/data")
    public String getDataKTP(Model model){
        int record = dataktpJpaController.getDataCount();
        String result = "";
        try {
            newdata = dataktpJpaController.findDataEntities().subList(0, record);
        } catch (Exception e) {
            result = e.getMessage();
        }
        
        model.addAttribute("goData", newdata);
        //model.addAttribute("record", record);
        
        return "database";
    }
}
