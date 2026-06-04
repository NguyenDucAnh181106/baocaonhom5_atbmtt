#include <iostream>
#include <iomanip>
#include <string>

using namespace std;

unsigned char state[4][4];

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

void inputState()
{
    int x;

    cout << "\nNhap 16 gia tri HEX:\n";

    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            cin >> hex >> x;
            state[i][j] = (unsigned char)x;
        }
    }

    cin >> dec;
}

void printState()
{
    cout << "\nKet qua:\n";

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
    cout << "1. Encrypt\n";
    cout << "2. Decrypt\n";
    cout << "3. Key Expansion\n";
    cout << "4. AES Simulation\n";
    cout << "0. Exit\n";
    cout << "============================\n";
    cout << "Nhap lua chon: ";
}

int main()
{
    int choice;

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
        cin >> choice;

        switch(choice)
        {
            case 1:
            {
                cout << "\nAES Encrypt\n";

                inputState();

                AddRoundKey(roundKey);

                printState();
                break;
            }

            case 2:
            {
                cout << "\nAES Decrypt\n";

                inputState();

                AESDecrypt(roundKey);

                printState();
                break;
            }

            case 3:
            {
                unsigned char key[16] =
                {
                    0x00,0x01,0x02,0x03,
                    0x04,0x05,0x06,0x07,
                    0x08,0x09,0x0A,0x0B,
                    0x0C,0x0D,0x0E,0x0F
                };

                unsigned char expandedKey[176];

                KeyExpansion(key,expandedKey);

                PrintRoundKey(expandedKey);

                break;
            }

            case 4:
            {
                AESSimulation();
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
