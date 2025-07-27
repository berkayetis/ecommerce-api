package com.berkayyetis.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date_created", insertable = false, updatable = false)
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.MERGE},
            fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<CartItem> items = new LinkedHashSet<>();

    // LOGICS
    public BigDecimal getTotalPrice(){
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CartItem getCartItem(Long productId){
        return items.stream()
                .filter(e -> e.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public CartItem addCartItem(Product product){
        CartItem cartItem = getCartItem(product.getId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(this);
            cartItem.setQuantity(1);
            items.add(cartItem);
        }
        else{
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }
        return cartItem;
    }

    public void removeCartItem(Long productId){
        CartItem cartItem = getCartItem(productId);
        if (cartItem != null) {
            items.remove(cartItem);
            cartItem.setCart(null);
        }
    }
    public void removeAllCartItems(){
        for (CartItem cartItem : items) {
            cartItem.setCart(null);
        }
        items.clear();
    }
}