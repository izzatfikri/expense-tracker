package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.service.ExpenseService;
import com.example.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ExpenseController {
    // Handle Http and return view
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Expense> expenses = expenseService.getCurrentUserExpenses();
        // Total amount
        BigDecimal totalAmount = expenses.stream()
                .map(expense -> expense.getAmount() != null ? expense.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("expenses", expenses);
        model.addAttribute("totalAmount", totalAmount);

        return "index";

    }

    @GetMapping("/addExpense")
    public String showAddExpensePage(Model model) {
        Expense expense = new Expense();
        model.addAttribute("expense", expense);
        return "add-expense";
    }

    @PostMapping("/saveExpense")
    public String saveExpense(@ModelAttribute("expense") Expense expense, Model model) {
        expenseService.saveExpense(expense);
        return "redirect:/";
    }

    @GetMapping("/editExpense/{id}")
    public String showEditExpensePage(Model model, @PathVariable("id") long id) {
        Expense expense = expenseService.getExpenseById(id);
        model.addAttribute("expense", expense);
        return "update-expense";
    }

    @PostMapping("/updateExpense/{id}")
    public String updateExpense(@PathVariable("id") long id, @ModelAttribute("expense") Expense expense) {
        Expense existingExpense = expenseService.getExpenseById(id);
        existingExpense.setDescription(expense.getDescription());
        existingExpense.setAmount(expense.getAmount());
        expenseService.saveExpense(existingExpense);
        return "redirect:/";
    }

    @GetMapping("/deleteExpense/{id}")
    public String deleteExpense(@PathVariable("id") long id) {
        expenseService.deleteExpenseById(id);
        return "redirect:/";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }


    @GetMapping("/login")
    public String showLoginPage()
    {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.registerNewUser(user);
        return "redirect:/login?registered";
    }


    // Test to update monthly budget
    @PostMapping("/updateBudget")
    public String updateBudget(@RequestParam("monthly_budget") Double monthly_budget) {
        User currentUser = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        userService.updateMonthlyBudget(currentUser.getId(), monthly_budget);
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("password", user.getPassword());
        model.addAttribute("monthly_budget", user.getMonthly_budget());
        return "profile";
    }


}
