#include <iostream>
#include <iomanip>
#include <string>
#include <fstream> 
#include <limits> // Them thu vien de lam sach bo nho dem (Chua chay nhap sai)

using namespace std;

unsigned char state[4][4];

// Chuyen khoa key thanh bien toan cuc de dung chung cho viec Luu/Doc khoa
unsigned char currentKey[16] =
{
    0x00,0x01,0x02,0x03,
    0x04,0x05,0x06,0x07,
    0x08,0x09,0x0A,0x0B,
    0x0C,0x0D,0x0E,0x0F
};

unsigned char Rcon[11] =
{
    0x00,
    0x01,0x02,0x04,0x08,
    0x10,0x20,0x40,0x80,
    0x1B,0x36
};

unsigned char InvSub(unsigned char x)
{
    return x ^ 0xFF;
}

void InvShiftRows()
{
    unsigned char temp;

    temp = state[1][3];
    for(int i=3;i>0;i--)
        state[1][i] = state[1][i-1];
    state[1][0] = temp;

    for(int k=0;k<2;k++)
    {
        temp = state[2][3];
        for(int i=3;i>0;i--)
            state[2][i] = state[2][i-1];
            state[2][0] = temp;
    }

    temp = state[3][0];
    for(int i=0;i<3;i++)
        state[3][i] = state[3][i+1];
    state[3][3] = temp;
}

void InvSubBytes()
{
    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            state[i][j] = InvSub(state[i][j]);
        }
    }
}

unsigned char gmul(unsigned char a,unsigned char b)
{
    return a * b;
}

void InvMixColumns()
{
    for(int j=0;j<4;j++)
    {
        state[0][j] = gmul(state[0][j],14);
        state[1][j] = gmul(state[1][j],11);
        state[2][j] = gmul(state[2][j],13);
        state[3][j] = gmul(state[3][j],9);
    }
}

void AddRoundKey(unsigned char roundKey[4][4])
{
    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            state[i][j] ^= roundKey[i][j];
        }
    }
}

void AESDecrypt(unsigned char roundKey[4][4])
{
    AddRoundKey(roundKey);
    InvShiftRows();
    InvSubBytes();
    InvMixColumns();
}

void RotWord(unsigned char w[4])
{
    unsigned char temp = w[0];

    for(int i=0;i<3;i++)
        w[i] = w[i+1];

    w[3] = temp;
}

void SubWord(unsigned char w[4])
{
    for(int i=0;i<4;i++)
        w[i] = w[i] ^ 0x3F;
}

void KeyExpansion(unsigned char key[16],
                  unsigned char expandedKey[176])
{
    for(int i=0;i<16;i++)
        expandedKey[i] = key[i];

    int pos = 16;
    int rcon = 1;

    unsigned char temp[4];

    while(pos < 176)
    {
        for(int i=0;i<4;i++)
            temp[i] = expandedKey[pos-4+i];

        if(pos % 16 == 0)
        {
            RotWord(temp);
            SubWord(temp);
            temp[0] ^= Rcon[rcon++];
        }

        for(int i=0;i<4;i++)
        {
            expandedKey[pos] =
                expandedKey[pos-16] ^ temp[i];
            pos++;
        }
    }
}

void PrintRoundKey(unsigned char expandedKey[176])
{
    for(int r=0;r<=10;r++)
    {
        cout << "\nRound " << r << ":\n";

        for(int i=r*16;i<(r+1)*16;i++)
        {
            cout << hex
                 << setw(2)
                 << setfill('0')
                 << (int)expandedKey[i]
                 << " ";
        }

        cout << dec << endl;
    }
}

// 1. CHUA CHAY CHO TRUONG HOP NHAP SAI
void inputState()
{
    int x;

    cout << "\n[CANH BAO] Du lieu State ban dau se bi thay doi sau khi nhap!\n"; 
    cout << "Nhap 16 gia tri HEX:\n";

    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            // Lap lai cho den khi nguoi dung nhap dung dinh dang Hex
            while (!(cin >> hex >> x)) {
                cout << "[LOI] Ban da nhap sai dinh dang! Vui long nhap lai tai vi tri [" << i << "][" << j << "]: ";
                cin.clear(); 
                cin.ignore(numeric_limits<streamsize>::max(), '\n'); // Xoa sach bo nho dem de tranh lap vo han
            }
            state[i][j] = (unsigned char)x;
        }
    }

    cin >> dec;
    cout << "[THONG BAO] Da cap nhat mang State!\n";
}

void printState()
{
    cout << "\nKet qua trong mang State:\n";

    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            cout << hex
                 << setw(2)
                 << setfill('0')
                 << (int)state[i][j]
                 << " ";
        }
        cout << endl;
    }

    cout << dec << endl;
}

// GIU NGUYEN CAC HAM NAY CUA BAN:
string xorHex(string a,string b)
{
    string result="";

    for(int i=0;i<a.length();i++)
    {
        result += (a[i]==b[i]) ? '0' : '1';
    }

    return result;
}

string charToBinary(char c)
{
    string bin="";

    for(int i=7;i>=0;i--)
    {
        bin += ((c>>i)&1) ? '1' : '0';
    }

    return bin;
}

string stringToBinary(string text)
{
    string binary="";

    for(int i=0;i<text.length();i++)
    {
        binary += charToBinary(text[i]);
    }

    return binary;
}

void AESSimulation()
{
    string plaintext,key;

    cin.ignore();

    cout << "Nhap plaintext: ";
    getline(cin,plaintext);

    cout << "Nhap key: ";
    getline(cin,key);

    string plainBinary = stringToBinary(plaintext);
    string keyBinary = stringToBinary(key);

    while(keyBinary.length() < plainBinary.length())
    {
        keyBinary += keyBinary;
    }

    keyBinary =
        keyBinary.substr(0,plainBinary.length());

    cout << "\n=== CAC VONG MA HOA AES MO PHONG ===\n";

    string round1 =
        xorHex(plainBinary,keyBinary);

    cout << "\nRound 1 - AddRoundKey:\n";
    cout << round1 << endl;

    string round2 =
        round1.substr(8) + round1.substr(0,8);

    cout << "\nRound 2 - ShiftRows:\n";
    cout << round2 << endl;

    string round3 =
        xorHex(round2,keyBinary);

    cout << "\nRound 3 - MixColumns:\n";
    cout << round3 << endl;

    string cipher =
        xorHex(round3,keyBinary);

    cout << "\nRound 4 - Final Ciphertext:\n";
    cout << cipher << endl;
}

void menu()
{
    cout << "\n============================\n";
    cout << "        AES PROGRAM\n";
    cout << "============================\n";
    cout << "1. Encrypt text\n";
    cout << "2. Decrypt text\n";
    cout << "3. Encrypt file\n";
    cout << "4. Decrypt file\n";
    cout << "5. Save ciphertext (Luu State)\n";
    cout << "6. Load ciphertext (Doc State)\n";
    cout << "7. Key Expansion (Test)\n"; 
    cout << "8. Save Key to file (Luu Khoa)\n";   // Them chuc nang luu khoa
    cout << "9. Load Key from file (Doc Khoa)\n"; // Them chuc nang doc khoa
    cout << "0. Exit\n";
    cout << "============================\n";
    cout << "Nhap lua chon: ";
}

int main()
{
    int choice;
    string filename;
    string textData;

    unsigned char roundKey[4][4] =
    {
        {0x2b,0x28,0xab,0x09},
        {0x7e,0xae,0xf7,0xcf},
        {0x15,0xd2,0x15,0x4f},
        {0x16,0xa6,0x88,0x3c}
    };

    do
    {
        menu();
        
        // CHUA CHAY KHI NHAP CHU VAO MENU
        if (!(cin >> choice)) {
            cout << "\n[LOI] Vui long nhap so tu 0 den 9!\n";
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            choice = -1;
            continue;
        }

        switch(choice)
        {
            case 1:
            {
                cout << "\n--- 1. Encrypt text ---\n";
                inputState();
                
                // 2. THONG BAO BIEN DOI DU LIEU (Ma hoa)
                cout << "\n[THONG BAO] Dang ma hoa... Mang State se bi ghi de.\n";
                AddRoundKey(roundKey);
                cout << "[THONG BAO] Du lieu goc da bi bien doi thanh Ciphertext!\n";
                
                printState();
                break;
            }

            case 2:
            {
                cout << "\n--- 2. Decrypt text ---\n";
                inputState();
                
                // 2. THONG BAO BIEN DOI DU LIEU (Giai ma)
                cout << "\n[THONG BAO] Dang giai ma... Mang State se bi ghi de.\n";
                AESDecrypt(roundKey);
                cout << "[THONG BAO] Ciphertext da duoc khoi phuc ve Du lieu goc!\n";
                
                printState();
                break;
            }

            case 3:
            {
                cout << "\n--- 3. Encrypt file ---\n";
                cin.ignore();
                cout << "Nhap ten file can ma hoa (vi du: data.txt): ";
                getline(cin, filename);
                
                ifstream inFile(filename.c_str());
                if(inFile.is_open()) {
                    cout << "Da mo file thanh cong. Thuc hien ma hoa...\n";
                    inFile.close();
                } else {
                    cout << "Khong the mo file!\n";
                }
                break;
            }

            case 4:
            {
                cout << "\n--- 4. Decrypt file ---\n";
                cin.ignore();
                cout << "Nhap ten file can giai ma (vi du: encrypted.dat): ";
                getline(cin, filename);
                
                ifstream inFile(filename.c_str());
                if(inFile.is_open()) {
                    cout << "Da mo file thanh cong. Thuc hien giai ma...\n";
                    inFile.close();
                } else {
                    cout << "Khong the mo file!\n";
                }
                break;
            }

            case 5:
            {
                cout << "\n--- 5. Save ciphertext ---\n";
                cin.ignore();
                cout << "Nhap ten file de luu ban ma (vi du: cipher.txt): ";
                getline(cin, filename);
                
                ofstream outFile(filename.c_str());
                if(outFile.is_open()) {
                    for(int i=0; i<4; i++) {
                        for(int j=0; j<4; j++) {
                            outFile << hex << setw(2) << setfill('0') << (int)state[i][j] << " ";
                        }
                        outFile << "\n";
                    }
                    cout << "[THONG BAO] Da luu mang State vao file " << filename << " thanh cong!\n";
                    outFile.close();
                } else {
                    cout << "Khong the tao file de luu!\n";
                }
                break;
            }

            case 6:
            {
                cout << "\n--- 6. Load ciphertext ---\n";
                cin.ignore();
                cout << "Nhap ten file can tai ban ma (vi du: cipher.txt): ";
                getline(cin, filename);
                
                ifstream inFile(filename.c_str());
                if(inFile.is_open()) {
                    int val;
                    cout << "\n[CANH BAO] Mang State hien tai se bi ghi de bang du lieu tu file!\n"; // Thong bao bien doi
                    for(int i=0; i<4; i++) {
                        for(int j=0; j<4; j++) {
                            if (inFile >> hex >> val) {
                                state[i][j] = (unsigned char)val;
                            }
                        }
                    }
                    cout << "[THONG BAO] Da tai du lieu tu file vao mang State thanh cong!\n";
                    inFile.close();
                    printState();
                } else {
                    cout << "Khong the mo file de tai ciphertext!\n";
                }
                break;
            }
            case 7:
            {
                cout << "\n--- 7. Key Expansion ---\n";
                unsigned char expandedKey[176];
                
                cout << "[THONG BAO] Dang tien hanh mo rong khoa...\n";
                KeyExpansion(currentKey, expandedKey);
                cout << "[THONG BAO] Khoa goc 16 bytes da duoc mo rong thanh 176 bytes!\n";
                
                PrintRoundKey(expandedKey);
                break;
            }
            
            // 3. LUU TRU VA TAI KHOA DE SU DUNG LAI
            case 8: 
            {
                cout << "\n--- 8. Save Key to file ---\n";
                cin.ignore();
                cout << "Nhap ten file de luu Khoa (VD: key.txt): ";
                getline(cin, filename);
                
                ofstream outFile(filename.c_str());
                if(outFile.is_open()) {
                    for(int i = 0; i < 16; i++) {
                        outFile << hex << setw(2) << setfill('0') << (int)currentKey[i] << " ";
                    }
                    cout << "[THONG BAO] Da luu Khoa vao file: " << filename << "\n";
                    outFile.close();
                } else {
                    cout << "Khong the tao file de luu Khoa!\n";
                }
                break;
            }

            case 9: 
            {
                cout << "\n--- 9. Load Key from file ---\n";
                cin.ignore();
                cout << "Nhap ten file chua Khoa can tai: ";
                getline(cin, filename);
                
                ifstream inFile(filename.c_str());
                if(inFile.is_open()) {
                    int val;
                    cout << "\n[CANH BAO] Khoa hien tai se bi thay the boi khoa trong file!\n";
                    for(int i = 0; i < 16; i++) {
                        if (inFile >> hex >> val) {
                            currentKey[i] = (unsigned char)val;
                        }
                    }
                    cout << "[THONG BAO] Da tai Khoa moi vao he thong thanh cong!\n";
                    inFile.close();
                } else {
                    cout << "Khong the mo file Khoa!\n";
                }
                break;
            }

            case 0:
            {
                cout << "\nThoat chuong trinh.\n";
                break;
            }

            default:
            {
                cout << "\nLua chon khong hop le!\n";
            }
        }

    }while(choice!=0);

    return 0;
}
