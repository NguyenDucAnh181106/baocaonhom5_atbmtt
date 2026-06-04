#Java VERSION
This folder contains the Java implementation of AES encryption and decryption.
Contributors: Nguyen Duc Anh - Doan Duy Hong Khanh

## AES-128 ECB NoPadding program

Files:

- `src/Main.java`: menu chinh, doc input va goi cac module.
- `src/AesEncryption.java`: xu ly ma hoa AES.
- `src/AesDecryption.java`: xu ly giai ma AES.
- `src/KeyManager.java`: kiem tra va tao khoa AES-128.
- `src/HexUtils.java`: chuyen doi giua byte array va chuoi hex.
- `out/`: thu muc chua file `.class` sau khi bien dich.

The program provides a menu for both encryption and decryption using
`AES/ECB/NoPadding`. Encryption reads plaintext and a 16-byte key, then prints
the ciphertext as uppercase hex. Decryption reads hex ciphertext and the same
16-byte key, then prints the plaintext.

Because `NoPadding` is used, the plaintext byte length must be a positive
multiple of 16 bytes.

Compile:

```bash
javac -d out src\Main.java src\AesEncryption.java src\AesDecryption.java src\KeyManager.java src\HexUtils.java
```

Run:

```bash
java -cp out Main
```

Example encrypt input:

```text
Choose: 1
Plaintext: 0123456789ABCDEF
Key (16 bytes): 1234567890ABCDEF
```

Example encrypt output:

```text
Ciphertext (hex): 3EA5BFF04867CF74E0F4A6EB1C2B675D
```

Example decrypt input:

```text
Choose: 2
Ciphertext (hex): 3EA5BFF04867CF74E0F4A6EB1C2B675D
Key (16 bytes): 1234567890ABCDEF
```

Example decrypt output:

```text
Plaintext: 0123456789ABCDEF
```
