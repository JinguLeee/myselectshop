package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.FolderRequestDto;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.exception.RestApiException;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/folders")
    public ResponseEntity addFolders(
            @RequestBody FolderRequestDto folderRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        try {
            List<String> folderNames = folderRequestDto.getFolderNames();
            User user = userDetails.getUser();

            List<Folder> folders = folderService.addFolders(folderNames, user.getUsername());
            return new ResponseEntity(folders, HttpStatus.OK);
        } catch(IllegalArgumentException ex) {
            RestApiException restApiException = new RestApiException();
            restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
            restApiException.setErrorMessage(ex.getMessage());
            return new ResponseEntity(
                    // HTTP body
                    restApiException,
                    // HTTP status code
                    HttpStatus.BAD_REQUEST);
        }
    }

    // 회원이 등록한 모든 폴더 조회
    @GetMapping("/folders")
    public List<Folder> getFolders(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return folderService.getFolders(userDetails.getUser());
    }

    // 회원이 등록한 폴더 내 모든 상품 조회
    @GetMapping("/folders/{folderId}/products")
    public Page<Product> getProductsInFolder(
            @PathVariable Long folderId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return folderService.getProductsInFolder(
                folderId,
                page-1,
                size,
                sortBy,
                isAsc,
                userDetails.getUser()
        );
    }


}