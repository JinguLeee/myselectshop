package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.ApiUseTime;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.ApiUseTimeRepository;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ApiUseTimeRepository apiUseTimeRepository;

    // 관심 상품 등록하기
    //@Secured(UserRoleEnum.Authority.ADMIN) // 관리자만 등록할 수 있게 제한을 걸음
    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 측정 시작 시간
        long startTime = System.currentTimeMillis();
        try {
            return productService.createProduct(requestDto, userDetails.getUser());
        } finally {
            // 측정 종료 시간
            long endTime = System.currentTimeMillis();
            // 수행시간 = 종료 시간 - 시작 시간
            long runTime = endTime - startTime;

            // 로그인 회원 정보
            User loginUser = userDetails.getUser();

            // API 사용시간 및 DB 에 기록
            ApiUseTime apiUseTime = apiUseTimeRepository.findByUser(loginUser)
                    .orElse(null);
            if (apiUseTime == null) {
                // 로그인 회원의 기록이 없으면
                apiUseTime = new ApiUseTime(loginUser, runTime);
            } else {
                // 로그인 회원의 기록이 이미 있으면
                apiUseTime.addUseTime(runTime);
            }

            log.info("[API Use Time] Username: " + loginUser.getUsername() + ", Total Time: " + apiUseTime.getTotalTime() + " ms");
            apiUseTimeRepository.save(apiUseTime);
        }
    }

    // 관심 상품 조회하기
    @GetMapping("/products")
    public Page<Product> getProducts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // 응답 보내기
        return productService.getProducts(userDetails.getUser(), page-1, size, sortBy, isAsc);
    }

    // 관심 상품 최저가 등록하기
    @PutMapping("/products/{id}")
    public Long updateProduct(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 응답 보내기 (업데이트된 상품 id)
        return productService.updateProduct(id, requestDto, userDetails.getUser());
    }

    // 상품에 폴더 추가
    @PostMapping("/products/{productId}/folder")
    public Long addFolder(
            @PathVariable Long productId,
            @RequestParam Long folderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Product product = productService.addFolder(productId, folderId, userDetails.getUser());
        return product.getId();
    }

}