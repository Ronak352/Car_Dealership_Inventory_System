package com.dealership.dto.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {


    private Long id;

    private String customerName;

    private String vehicleName;
}