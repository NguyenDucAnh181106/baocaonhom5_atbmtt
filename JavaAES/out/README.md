# Phiên bản Java
Thư mục này chứa chương trình mã hóa và giải mã AES bằng Java.
Thành viên: Nguyen Duc Anh - Doan Duy Hong Khanh

## Chương trình AES-128 ECB

Các file chính:

- `src/Main.java`: menu chinh, doc input va goi cac module.
- `src/GuiMain.java`: giao dien Swing co mo/luu file, sinh/lưu/mo khoa, ma hoa va giai ma.
- `src/AesEncryption.java`: xu ly ma hoa AES.
- `src/AesDecryption.java`: xu ly giai ma AES.
- `src/KeyManager.java`: kiem tra va tao khoa AES-128.
- `src/HexUtils.java`: chuyen doi giua mang byte va chuoi hex.
- `out/`: thu muc chua file `.class` sau khi bien dich.

Chương trình có menu console và giao diện Swing để mã hóa/giải mã bằng
`AES/ECB/PKCS5Padding`. Mã hóa nhận bản rõ và khóa 16 byte, sau đó trả về bản mã
dạng hex in hoa. Giải mã nhận bản mã hex và cùng khóa đó để khôi phục bản rõ.

Giao diện Swing hỗ trợ mở/lưu file bản rõ, mở/lưu file bản mã, sinh khóa tự động,
lưu khóa và mở khóa từ file.

Biên dịch:

```bash
javac -encoding UTF-8 -d out src\Main.java src\GuiMain.java src\AesEncryption.java src\AesDecryption.java src\KeyManager.java src\HexUtils.java src\IntegrityUtils.java
```

Chạy console:

```bash
java -cp out Main
```

Chạy giao diện:

```bash
java -cp out GuiMain
```

Ví dụ nhập mã hóa:

```text
Chọn chức năng: 1
Bản rõ: 0123456789ABCDEF
Khóa (16 byte): 1234567890ABCDEF
```

Ví dụ kết quả mã hóa:

```text
Bản mã (hex): 3EA5BFF04867CF74E0F4A6EB1C2B675D
```

Ví dụ nhập giải mã:

```text
Chọn chức năng: 2
Bản mã (hex): 3EA5BFF04867CF74E0F4A6EB1C2B675D
Khóa (16 byte): 1234567890ABCDEF
```

Ví dụ kết quả giải mã:

```text
Bản rõ: 0123456789ABCDEF
```
