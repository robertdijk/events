package ch.wisv.events.dashboard.controller;

import ch.wisv.events.core.exception.EventsInvalidModelException;
import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.exception.ProductNotFound;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.product.SoldProductService;
import ch.wisv.events.core.webhook.WebhookPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * DashboardProductController.
 */
@Controller
@RequestMapping("/dashboard/products")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardProductController {

    /**
     * ProductService
     */
    private final ProductService productService;

    /**
     * Field orderService
     */
    private final SoldProductService soldProductService;

    /**
     * Field webhookPublisher
     */
    private final WebhookPublisher webhookPublisher;

    /**
     * Default constructor
     *
     * @param productService     of type ProductService.
     * @param soldProductService of type SoldProductService.
     * @param webhookPublisher   of type WebhookPublisher.
     */
    @Autowired
    public DashboardProductController(ProductService productService,
            SoldProductService soldProductService,
            WebhookPublisher webhookPublisher
    ) {
        this.productService = productService;
        this.soldProductService = soldProductService;
        this.webhookPublisher = webhookPublisher;
    }

    /**
     * Get request for ProductOverview
     *
     * @param model SpringUI model
     * @return thymeleaf template path
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", this.productService.getAllProducts());

        return "dashboard/products/index";
    }

    /**
     * Method view ...
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @return String
     */
    @GetMapping("/view/{key}/")
    public String view(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            model.addAttribute("product", this.productService.getByKey(key));

            return "dashboard/products/view";
        } catch (EventsModelNotFound e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/dashboard/products/";
        }
    }

    /**
     * Get request to create a Product
     *
     * @param model SpringUI model
     * @return thymeleaf template path
     */
    @GetMapping("/create/")
    public String create(Model model) {
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new Product());
        }

        return "dashboard/products/product";
    }

    /**
     * Post request to create a Product
     *
     * @param product  Product product attr.
     * @param redirect Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/create/")
    public String create(RedirectAttributes redirect, @ModelAttribute Product product) {
        try {
            this.productService.create(product);
            this.webhookPublisher.createWebhookTask(WebhookTrigger.PRODUCT_CREATE_UPDATE, product);
            redirect.addFlashAttribute("message", "Product " + product.getTitle() + " has been successfully created!");

            return "redirect:/dashboard/products/view/" + product.getKey() + "/";
        } catch (EventsInvalidModelException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("product", product);

            return "redirect:/dashboard/products/create/";
        }
    }

    /**
     * Get request to edit a Product or if the key does not exists it will redirect to the
     * Product Overview page
     *
     * @param model SpringUI model
     * @return thymeleaf template path
     */
    @GetMapping("/edit/{key}/")
    public String edit(Model model, @PathVariable String key) {
        try {
            if (!model.containsAttribute("product")) {
                model.addAttribute("product", this.productService.getByKey(key));
            }

            return "dashboard/products/product";
        } catch (EventsModelNotFound e) {
            return "redirect:/dashboard/products/";
        }
    }

    /**
     * Method edit post request to update an existing Product
     *
     * @param redirect of type RedirectAttributes
     * @param product  of type Product
     * @return String
     */
    @PostMapping("/edit/{key}/")
    public String update(RedirectAttributes redirect, @ModelAttribute Product product) {
        try {
            this.productService.update(product);
            this.webhookPublisher.createWebhookTask(WebhookTrigger.PRODUCT_CREATE_UPDATE, product);
            redirect.addFlashAttribute("success", "Changes have been saved!");

            return "redirect:/dashboard/products/view/" + product.getKey() + "/";
        } catch (EventsModelNotFound | EventsInvalidModelException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("product", product);

            return "redirect:/dashboard/products/edit/" + product.getKey() + "/";
        }
    }

    /**
     * Method overview will show a list of the users with this product.
     *
     * @param model of type Model
     * @param key   of type String
     * @return String
     */
    @GetMapping("/overview/{key}/")
    public String overview(Model model, @PathVariable String key) {
        try {
            Product product = productService.getByKey(key);

            model.addAttribute("soldProducts", soldProductService.getByProduct(product));
            model.addAttribute("product", product);

            return "dashboard/products/overview";
        } catch (ProductNotFound e) {
            return "redirect:/dashboard/products/";
        }
    }

    /**
     * Get request to delete a Product
     *
     * @param redirectAttributes Spring RedirectAttributes
     * @param key                key of a Product
     * @return redirect
     */
    @GetMapping("/delete/{key}/")
    public String delete(RedirectAttributes redirectAttributes, @PathVariable String key) {
        Product product = productService.getByKey(key);
        try {
            this.productService.delete(product);
            this.webhookPublisher.createWebhookTask(WebhookTrigger.PRODUCT_DELETE, product);

            redirectAttributes.addFlashAttribute("message", "Product " + product.getTitle() + " has been deleted!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard/products/";
    }
}
