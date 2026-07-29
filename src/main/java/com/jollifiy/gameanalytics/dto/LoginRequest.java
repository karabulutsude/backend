/*
Neden DTO (Data Transfer Object) kullanıyoruz?

Çünkü Entity veritabanını temsil eder, DTO ise istemci (Unity) ile haberleşmeyi temsil eder.

Bunu aklında şöyle tutabilirsin:

- Entity → PostgreSQL'de nasıl saklanacağını anlatır.
- DTO → Unity ile hangi bilgilerin gidip geleceğini anlatır.

Gerçek projelerde bu ayrım çok önemlidir.
 */


/*
Bu sınıf neden var?

Unity bize login olurken sadece deviceId gönderecek.

Yani Unity'nin göndereceği JSON:
{
    "deviceId":"ABC123XYZ"
}
Spring Boot bunu otomatik olarak LoginRequest nesnesine dönüştürecek.

Yani JSON ile uğraşmayacağız.
 */


package com.jollifiy.gameanalytics.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    private String deviceId;
    private String country;

}
