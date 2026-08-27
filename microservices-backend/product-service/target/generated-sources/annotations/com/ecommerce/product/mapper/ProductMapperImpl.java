package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ProductDtos;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-12T16:09:50+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDtos.CategoryResponse toDto(Category category) {
        if ( category == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;

        id = category.getId();
        name = category.getName();
        description = category.getDescription();

        ProductDtos.CategoryResponse categoryResponse = new ProductDtos.CategoryResponse( id, name, description );

        return categoryResponse;
    }

    @Override
    public Category toEntity(ProductDtos.CategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.name( request.name() );
        category.description( request.description() );

        return category.build();
    }

    @Override
    public ProductDtos.ProductResponse toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        BigDecimal price = null;
        String imageUrl = null;
        String brand = null;
        boolean active = false;
        ProductDtos.CategoryResponse category = null;
        LocalDateTime createdAt = null;

        id = product.getId();
        name = product.getName();
        description = product.getDescription();
        price = product.getPrice();
        imageUrl = product.getImageUrl();
        brand = product.getBrand();
        active = product.isActive();
        category = toDto( product.getCategory() );
        createdAt = product.getCreatedAt();

        ProductDtos.ProductResponse productResponse = new ProductDtos.ProductResponse( id, name, description, price, imageUrl, brand, active, category, createdAt );

        return productResponse;
    }

    @Override
    public ProductDtos.ProductSummary toSummary(Product product) {
        if ( product == null ) {
            return null;
        }

        String categoryName = null;
        Long id = null;
        String name = null;
        BigDecimal price = null;
        String imageUrl = null;
        String brand = null;

        categoryName = productCategoryName( product );
        id = product.getId();
        name = product.getName();
        price = product.getPrice();
        imageUrl = product.getImageUrl();
        brand = product.getBrand();

        ProductDtos.ProductSummary productSummary = new ProductDtos.ProductSummary( id, name, price, imageUrl, brand, categoryName );

        return productSummary;
    }

    @Override
    public Product toEntity(ProductDtos.ProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.name( request.name() );
        product.description( request.description() );
        product.price( request.price() );
        product.imageUrl( request.imageUrl() );
        product.brand( request.brand() );

        product.active( true );

        return product.build();
    }

    @Override
    public void updateEntity(ProductDtos.ProductRequest request, Product product) {
        if ( request == null ) {
            return;
        }

        product.setName( request.name() );
        product.setDescription( request.description() );
        product.setPrice( request.price() );
        product.setImageUrl( request.imageUrl() );
        product.setBrand( request.brand() );
    }

    private String productCategoryName(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
