package edu.poly.lab5.controller;

import edu.poly.lab5.model.Category;
import edu.poly.lab5.repository.CategotyRepository;
import jakarta.validation.Valid;
import org.hibernate.query.results.ResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("categories")
public class CategoryController {
    @Autowired
    private CategotyRepository categotyRepository;

    @GetMapping("list")
    public String list(Model model, @RequestParam Optional<String> message){
        Iterable<Category> list=categotyRepository.findAll();
        if(message.isPresent()){
            model.addAttribute("message",message.get());
        }
         model.addAttribute("categories",list);
        return "categories/list";
    }

    @GetMapping("sort")
    public String sort(Model model, @RequestParam Optional<String> message, @SortDefault(sort = "name",direction = Sort.Direction.ASC) Sort sort){
        Iterable<Category> list=categotyRepository.findAll(sort);
        if(message.isPresent()){
            model.addAttribute("message",message.get());
        }
        model.addAttribute("categories",list);
        return "categories/sort";
    }

    @GetMapping("paginate")
    public String paginate(Model model, @RequestParam Optional<String> message,@PageableDefault(size = 5,sort = "name",direction = Sort.Direction.ASC) Pageable pageable){
         Page<Category> pages =categotyRepository.findAll(pageable);
        if(message.isPresent()){
            model.addAttribute("message",message.get());
        }

        List<Sort.Order> sortOders=pages.getSort().stream().collect(Collectors.toList());
        if(sortOders.size()>0){
            Sort.Order order=sortOders.get(0);
            model.addAttribute("sort",order.getProperty()+","+(order.getDirection()==Sort.Direction.ASC?"asc":"desc"));
        }else{
            model.addAttribute("sort","name");
        }
        model.addAttribute("pages",pages);
        return "categories/paginate";
    }

    @GetMapping(value = {"newOrEdit","newOrEdit/{id}"})
    public String newOrEdit(Model model, @PathVariable(name = "id",required = false) Optional<Long> id){
        Category category;
        if(id.isPresent()){
            Optional<Category> existedCate=categotyRepository.findById(id.get());
            category=existedCate.isPresent()?existedCate.get():new Category();
        }else {
            category=new Category();
        }
        model.addAttribute("category",category);
        return "categories/newOrEdit";
    }

    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(RedirectAttributes attributes, @Valid Category item, BindingResult result, ModelMap modelMap){
        if(result.hasErrors()){
            modelMap.addAttribute("category",item);
            return "categories/newOrEdit";
        }
        categotyRepository.save(item);
        attributes.addAttribute("message","New Category is saved");
        return "redirect:/categories/list";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        categotyRepository.deleteById(id);
        redirectAttributes.addAttribute("message","Delete success");
        return "redirect:/categories/list";
    }
}
