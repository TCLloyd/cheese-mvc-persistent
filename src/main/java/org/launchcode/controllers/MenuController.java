package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

import static java.awt.SystemColor.menu;

/**
 * Created by Tom on 4/13/2017.
 */
@Controller
@RequestMapping(value="menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value="", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value="/add", method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value="/add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        model.addAttribute("title", "Add Category");
        if (errors.hasErrors()) {
            return "menu/add";
        }
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value="/view/{id}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable int id) {
        Menu menu = menuDao.findOne(id);
        model.addAttribute("menu", menu);
        model.addAttribute("title", menu.getName());

        return "menu/view";
    }

    @RequestMapping(value="/add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, Menu menu, @PathVariable int id) {
        Menu menuItem = menuDao.findOne(id);
        model.addAttribute("title", "Add Item to Menu: " + menuItem.getName());
        AddMenuItemForm newForm = new AddMenuItemForm(menuItem, cheeseDao.findAll());
        model.addAttribute("form", newForm);
        return "menu/add-item";
    }

    @RequestMapping(value = "/add-item/{id}", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, @PathVariable int id, Errors errors) {
        if (errors.hasErrors()) {
            return "menu/add-item";
        }
        Menu menu = menuDao.findOne(form.getMenuId());
        Cheese cheese = cheeseDao.findOne(form.getCheeseId());
        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:../view/" + menu.getId();
    }
}
