# C++ Version.
This folder the C++ implementation of AES encryption and decryption.
Contributors: Truong Dinh Dat, Le Thanh Long, Nguyen Van Quang

Đây là chương trình mô phỏng thuật toán mã hóa AES-128 được viết bằng ngôn ngữ C++. Chương trình hỗ trợ các chức năng mã hóa, giải mã cơ bản, lưu/đọc dữ liệu từ file và quản lý khóa.

# Yêu cầu hệ thống (Requirements)
* Trình biên dịch C++ (G++, Clang, hoặc Visual Studio C++).
* Hệ điều hành: Windows, Linux hoặc macOS.

# Cách biên dịch và chạy chương trình (Setup & Run)

1. Biên dịch (Compile)
Mở terminal tại thư mục chứa file `ATBMTT_IT6002.cpp` và chạy lệnh sau:
```bash
g++ ATBMTT_IT6002.cpp -o aes_program
```
2. Chạy chương trình
Windows: ```bash
aes_program.exe

Linux: ./aes_program
macOS: ./aes_program

## # Các chức năng chính (Main Features)
Chương trình cung cấp một hệ thống Menu tương tác từ số 0 đến 9 giúp người dùng dễ dàng kiểm thử từng giai đoạn của thuật toán:
* **1. Encrypt text (Mã hóa văn bản):** Nhập ma trận Trạng thái (State) 4x4 bằng mã Hex và thực hiện bước biến đổi AddRoundKey ban đầu.
* **2. Decrypt text (Giải mã văn bản):** Đảo ngược quá trình mã hóa trên ma trận State với đầy đủ các hàm biến đổi lõi: AddRoundKey, InvShiftRows, InvSubBytes, và InvMixColumns.
* **3 & 4. Encrypt/Decrypt file (Xử lý tệp):** Kiểm tra đường dẫn hệ thống và mở tệp tin đầu vào để chuẩn bị cho luồng đọc/ghi dữ liệu mật mã.
* **5 & 6. Save/Load ciphertext (Lưu/Đọc State):** Xuất ma trận state[4][4] hiện tại ra file dưới dạng các ký tự Hex hoặc nạp ngược lại dữ liệu từ file bản mã vào hệ thống.
* **7. Key Expansion (Mở rộng khóa):** Thực hiện thuật toán sinh khóa vòng từ khóa gốc 16 bytes thành mảng khóa mở rộng 176 bytes, hỗ trợ in chi tiết khóa vòng từ Round 0 đến Round 10.
* **8 & 9. Save/Load Key (Quản lý khóa):** Lưu trữ khóa mật mã hiện tại (currentKey) ra file văn bản bên ngoài hoặc đọc file để tái cấu hình khóa mới cho chương trình.
* **0. Exit:** Thoát chương trình an toàn.

## # Cấu trúc dữ liệu cốt lõi (Core Data Structures)
* `state[4][4]`: Ma trận byte lưu trữ khối dữ liệu 16-byte (128-bit) đang được xử lý xuyên suốt các vòng biến đổi.
* `currentKey[16]`: Khóa mật mã gốc (128-bit) dùng để bắt đầu quá trình mã hóa và làm đầu vào cho hàm mở rộng khóa.
* `expandedKey[176]`: Mảng lưu trữ toàn bộ 11 khóa vòng (mỗi vòng 16 bytes) sau khi đi qua hàm xử lý KeyExpansion.

## # Cơ chế thuật toán & Lưu ý kỹ thuật (Technical Notes)
* **Cơ chế mô phỏng (Mocking):** Để tập trung vào việc mô phỏng luồng logic và cấu trúc phân tầng của thuật toán AES trong phạm vi bài thực hành, hàm `SubBytes`/`InvSubBytes` hiện đang sử dụng phép toán XOR mặt nạ bit định sẵn thay vì tra bảng S-Box tiêu chuẩn. Đồng thời, hàm `MixColumns` thực hiện biến đổi tuyến tính qua phép nhân số nguyên thông thường thay vì tính toán đa thức trên trường hữu hạn GF(2^8).
* **Xử lý ngoại lệ nhập liệu cực đoan:** Mã nguồn đã được tối ưu hóa bằng cách tích hợp các lệnh xóa bộ nhớ đệm (`cin.clear()` và `cin.ignore()`) tại Menu chính và hàm nhập liệu. Cơ chế này giúp ngăn chặn hoàn toàn hiện tượng lặp vô hạn (infinite loop) khi người dùng vô tình nhập sai định dạng (ví dụ: nhập ký tự chữ vào menu số).
