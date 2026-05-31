#include <iostream>
#include <iomanip>
using namespace std;

unsigned char sbox[256] = {
    0x63,0x7c,0x77,0x7b,
   
};


unsigned char Rcon[11] = {
    0x00,
    0x01,0x02,0x04,0x08,
    0x10,0x20,0x40,0x80,
    0x1B,0x36
};


void RotWord(unsigned char w[4]) {
    unsigned char temp = w[0];

    w[0] = w[1];
    w[1] = w[2];
    w[2] = w[3];
    w[3] = temp;
}

void SubWord(unsigned char w[4]) {
    for(int i = 0; i < 4; i++) {
        w[i] = sbox[w[i]];
    }
}

void KeyExpansion(unsigned char key[16],
                  unsigned char expandedKey[176]) {

    for(int i = 0; i < 16; i++) {
        expandedKey[i] = key[i];
    }

    int soByteDaSinh = 16;
    int chiSoRcon = 1;

    unsigned char temp[4];

    while(soByteDaSinh < 176) {

        for(int i = 0; i < 4; i++) {
            temp[i] = expandedKey[soByteDaSinh - 4 + i];
        }

        if(soByteDaSinh % 16 == 0) {

            RotWord(temp);

            SubWord(temp);

            temp[0] = temp[0] ^ Rcon[chiSoRcon];
            chiSoRcon++;
        }

        for(int i = 0; i < 4; i++) {

            expandedKey[soByteDaSinh] =
                expandedKey[soByteDaSinh - 16] ^ temp[i];

            soByteDaSinh++;
        }
    }
}

void InRoundKey(unsigned char expandedKey[176]) {

    for(int round = 0; round <= 10; round++) {

        cout << "\nRound Key " << round << ":\n";

        for(int i = round * 16;
            i < (round + 1) * 16;
            i++) {

            cout << hex
                 << setw(2)
                 << setfill('0')
                 << (int)expandedKey[i]
                 << " ";
        }

        cout << endl;
    }
}

int main() {

    unsigned char key[16] = {
        0x2b,0x7e,0x15,0x16,
        0x28,0xae,0xd2,0xa6,
        0xab,0xf7,0x15,0x88,
        0x09,0xcf,0x4f,0x3c
    };

    unsigned char expandedKey[176];

    KeyExpansion(key, expandedKey);

    InRoundKey(expandedKey);

    return 0;
}
