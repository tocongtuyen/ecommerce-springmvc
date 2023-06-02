package com.vietshop.controller.web;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vietshop.Entity.CartItem;
import com.vietshop.Entity.Category;
import com.vietshop.Service.impl.AccountService;
import com.vietshop.Service.impl.CartItemService;
import com.vietshop.Service.impl.CategoryService;
import com.vietshop.Service.impl.ProductService;
import com.vietshop.dto.AccountDTO;
import com.vietshop.dto.CategoryDTO;
import com.vietshop.dto.ProductDTO;
import com.vietshop.util.SecurityUtils;

@Controller(value = "productControllerOfWeb")
//@RequestMapping("/client")

public class productController {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private CartItemService cartItemService;

	@RequestMapping(value = "/shop-grid", method = RequestMethod.GET)
	public String shopGrid(Model model, @RequestParam("p") Optional<Integer> p,
			@RequestParam(name = "sort", defaultValue = "idProduct") Optional<String> sort,
			@RequestParam(name = "updown", defaultValue = "ASC") String updown,
			@RequestParam("keyword") Optional<String> keyword, HttpSession session) {
		List<Category> list = categoryService.findAll();
		model.addAttribute("category", list);
		int currentPage = p.orElse(0);
		Pageable pageable = null;
		if (updown.equals("ASC")) {
			PageRequest page_req = new PageRequest(currentPage, 12, Sort.Direction.ASC, sort.orElse("idProduct"));
			pageable = page_req;
		}
		if (updown.equals("DESC")) {
			PageRequest page_req = new PageRequest(currentPage, 12, Sort.Direction.DESC, sort.orElse("idProduct"));
			pageable = page_req;
		}
		Page<ProductDTO> productPage;
		if (keyword.isPresent()) {
			productPage = productService.searchProduct(keyword, pageable);// Thực hiện chức năng tìm kiếm sản phẩm

			model.addAttribute("keyword", keyword.get());

		} else {

			productPage = productService.findProducts("display", pageable);
		}
		model.addAttribute("product", productPage);
		long size = productPage.getTotalElements();
		System.out.println(size);
		model.addAttribute("size", size);
		model.addAttribute("sorter", sort.get());
		model.addAttribute("updown", updown);
		model.addAttribute("p", currentPage);

		// định dạng tiền tệ VND
		DecimalFormat formatter = new DecimalFormat("###,###,###.##");
		model.addAttribute("formatter", formatter);

		// code hiển thị thong tin giỏ hàng
		try {
			AccountDTO account = accountService.findByUserName(SecurityUtils.getPrincipal().getUsername());
			if (account != null) {
				List<CartItem> items = account.getCartItems();
				if (items == null) {
					model.addAttribute("quantity", 0);
					model.addAttribute("priceTotal", 0);

				} else {
					double priceTotal = 0;
					for (CartItem i : items) {
						priceTotal = priceTotal + i.getTotal();
					}
					session.setAttribute("total", formatter.format(priceTotal)); // set thông tin giỏ hàng lên header
					session.setAttribute("quantity", items.size());
				}
			}

		} catch (Exception e) {
			model.addAttribute("quantity", 0); // KHi chưa đăng nhập thì giỏ hàng = 0
			model.addAttribute("priceTotal", 0);
			// Get sp mới nhất list 1
			PageRequest page_req1 = new PageRequest(0, 3);
			Page<ProductDTO> lastProduct1 = productService.findLastProduct("display", page_req1);

			model.addAttribute("lastProduct1", lastProduct1);

			// Get sp mới nhất list 2
			PageRequest page_req2 = new PageRequest(1, 3);
			Page<ProductDTO> lastProduct2 = productService.findLastProduct("display", page_req2);

			model.addAttribute("lastProduct2", lastProduct2);
			return "web/shopGrid";
		}

		// Get sp mới nhất list 1
		PageRequest page_req1 = new PageRequest(0, 3);
		Page<ProductDTO> lastProduct1 = productService.findLastProduct("display", page_req1);

		model.addAttribute("lastProduct1", lastProduct1);

		// Get sp mới nhất list 2
		PageRequest page_req2 = new PageRequest(1, 3);
		Page<ProductDTO> lastProduct2 = productService.findLastProduct("display", page_req2);
		model.addAttribute("lastProduct2", lastProduct2);

		return "web/shopGrid";
	}

	
	@GetMapping("/shopGridByCategory")
	public String shopGridByCategory(ModelMap model, @RequestParam("p") Optional<Integer> p,
			@RequestParam(name = "idCategory", required = true) Long idCategory,
			@RequestParam(name = "sort", defaultValue = "idProduct") Optional<String> sort,
			@RequestParam(name = "updown", defaultValue = "ASC") String updown) {
		// Lấy list category để hiển thị ra view select
		List<Category> category = categoryService.findAll();

		int currentPage = p.orElse(0);
		Pageable pageable = null;

		if (updown.equals("ASC")) {
			PageRequest page_req = new PageRequest(currentPage, 12, Sort.Direction.ASC, sort.orElse("idProduct"));
			pageable = page_req;
		}
		if (updown.equals("DESC")) {
			PageRequest page_req = new PageRequest(currentPage, 12, Sort.Direction.DESC, sort.orElse("idProduct"));
			pageable = page_req;
		}
		Page<ProductDTO> productPage = productService.findAllByIdCategory("display", idCategory, pageable);
		long size = productPage.getTotalElements();
		System.out.println(size);
		model.addAttribute("size", size);
		model.addAttribute("product", productPage);
		model.addAttribute("idCategory", idCategory);
		model.addAttribute("category", category);
		CategoryDTO cate = categoryService.findOne(idCategory);
		model.addAttribute("cateName", cate);
		model.addAttribute("sorter", sort.get());
		System.out.println(sort.get());
		model.addAttribute("updown", updown);
		model.addAttribute("p", currentPage);

		// định dạng tiền tệ VND
		DecimalFormat formatter = new DecimalFormat("###,###,###.##");
		model.addAttribute("formatter", formatter);
		// code hiển thị thong tin giỏ hàng
		try {
			AccountDTO account = accountService.findByUserName(SecurityUtils.getPrincipal().getUsername());
			if (account != null) {
				List<CartItem> items = account.getCartItems();
				if (items == null) {
					model.addAttribute("quantity", 0);
					model.addAttribute("priceTotal", 0);

				} else {
					double priceTotal = 0;
					for (CartItem i : items) {
						priceTotal = priceTotal + i.getTotal();
					}
					model.addAttribute("priceTotal", priceTotal);

					model.addAttribute("quantity", items.size());
				}
			}
		} catch (Exception e) {
			model.addAttribute("quantity", 0); // KHi chưa đăng nhập thì giỏ hàng = 0
			model.addAttribute("priceTotal", 0);
			// Get sp mới nhất list 1
			PageRequest page_req1 = new PageRequest(0, 3);
			Page<ProductDTO> lastProduct1 = productService.findLastProduct("display", page_req1);

			model.addAttribute("lastProduct1", lastProduct1);

			// Get sp mới nhất list 2
			PageRequest page_req2 = new PageRequest(1, 3);
			Page<ProductDTO> lastProduct2 = productService.findLastProduct("display", page_req2);

			model.addAttribute("lastProduct2", lastProduct2);
			return "web/shopGridByCategory";
		}
		// Get sp mới nhất list 1
		PageRequest page_req1 = new PageRequest(0, 3);
		Page<ProductDTO> lastProduct1 = productService.findLastProduct("display", page_req1);

		model.addAttribute("lastProduct1", lastProduct1);

		// Get sp mới nhất list 2
		PageRequest page_req2 = new PageRequest(1, 3);
		Page<ProductDTO> lastProduct2 = productService.findLastProduct("display", page_req2);

		model.addAttribute("lastProduct2", lastProduct2);
		return "web/shopGridByCategory";
	}
	

	@GetMapping("/product-detail")
	public String productDetail(Model model, @RequestParam("idProduct") Long idProduct,
			@RequestParam(name = "idCategory", required = true) Long idCategory) {
		List<Category> list = categoryService.findAll();
		model.addAttribute("category", list);
		ProductDTO product = productService.findById(idProduct);
		double price = product.getCost();
		model.addAttribute("product", product);
		model.addAttribute("idProduct", idProduct);
		model.addAttribute("price", price);

		PageRequest page_req = new PageRequest(0, 9);
		Pageable pageable = page_req;
		// List sản phẩm tương tự
		Page<ProductDTO> productPage = productService.listRelatedProduct(idCategory, pageable, idProduct, "display");
		System.out.println(productPage);
		int size = productPage.getNumberOfElements();
		model.addAttribute("size", size);
		model.addAttribute("productByCate", productPage);

		// định dạng tiền tệ VND
		DecimalFormat formatter = new DecimalFormat("###,###,###.##");
		model.addAttribute("formatter", formatter);

		// code hiển thị thong tin giỏ hàng
		try {
			AccountDTO account = accountService.findByUserName(SecurityUtils.getPrincipal().getUsername());
			if (account != null) {
				List<CartItem> items = account.getCartItems();
				if (items == null) {
					model.addAttribute("quantity", 0);
					model.addAttribute("priceTotal", 0);

				} else {
					double priceTotal = 0;
					for (CartItem i : items) {
						priceTotal = priceTotal + i.getTotal();
					}
					model.addAttribute("priceTotal", priceTotal);

					model.addAttribute("quantity", items.size());
				}
			}
		} catch (Exception e) {
			model.addAttribute("quantity", 0); // KHi chưa đăng nhập thì giỏ hàng = 0
			model.addAttribute("priceTotal", 0);
			return "web/productDetails";
		}
		return "web/productDetails";
	}

	@GetMapping("/addProductDetail")
	public String addProductDetail(Model model, @RequestParam("idProduct") Long idProduct,
			@RequestParam("quantity") Long quantity, HttpSession session) {

		try {
			cartItemService.doAddProductToCart(idProduct, quantity);
		} catch (Exception e) {
			return "redirect:/authen";
		}
		model.addAttribute("idProduct", idProduct);
		model.addAttribute("idCategory", productService.findByIdProduct(idProduct).get().getCategory().getIdCategory());
		// định dạng tiền tệ VND
		DecimalFormat formatter = new DecimalFormat("###,###,###.##");
		model.addAttribute("formatter", formatter);

		try {
			AccountDTO account = accountService.findByUserName(SecurityUtils.getPrincipal().getUsername());
			if (account != null) {
				List<CartItem> items = account.getCartItems();
				if (items == null) {
					model.addAttribute("quantity", 0);
					model.addAttribute("priceTotal", 0);

				} else {
					double priceTotal = 0;
					for (CartItem i : items) {
						priceTotal = priceTotal + i.getTotal();
					}
					session.setAttribute("total", formatter.format(priceTotal)); // set thông tin giỏ hàng lên header
					session.setAttribute("quantity", items.size());
				}
			}

		} catch (Exception e) {
			model.addAttribute("quantity", 0); // KHi chưa đăng nhập thì giỏ hàng = 0
			model.addAttribute("priceTotal", 0);
		}
		return "redirect:/product-detail";
	}
}
