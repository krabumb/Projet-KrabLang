Algorithme
    debut
        entier n = 0;
        lire(n);
        si n <= 0
            ecrire(0);
        sinon
            entier i = 1;
            entier a = 0;
            entier b = 1;
            entier c = a+b;
            tantque i < n
                c = a+b;
                a = b;
                b = c;
                i = i + 1;
            ftant
            ecrire(c);
        fsi
    fin