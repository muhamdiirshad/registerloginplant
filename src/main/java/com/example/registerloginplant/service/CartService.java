package com.example.registerloginplant.service;


import com.example.registerloginplant.entity.Cart;
import com.example.registerloginplant.entity.Product;
import com.example.registerloginplant.repository.CartRepository;
import com.example.registerloginplant.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final ProductRepository productRepository;

    @Autowired
    public CartService(ProductRepository productRepository, CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }





    public Cart getCartFromSession(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
    private final CartRepository cartRepository;

    public void removeFromCartAndSave(Long productId, HttpSession session) {
        Cart cart = getCart(session);
        cart.removeItem(productId); // Assuming you're removing by productId
        saveCartToDatabase(cart); // Save the updated cart to the database
        session.setAttribute("cart", cart); // Update cart in session after removing item
    }





    public Cart getCartByUserId(long id) {
        return null;
    }


    public void addToCart(Long productId, HttpSession session) {
        Cart cart = getCart(session);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        cart.addItem(product);
        session.setAttribute("cart", cart); // Update cart in session after adding item
    }



    public double getCartTotal(HttpSession session) {
        return getCart(session).getTotal();
    }

    public void saveCartToDatabase(Cart cart) {
        cartRepository.save(cart);
    }

    public Cart retrieveCartFromDatabase(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }


}


