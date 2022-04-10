# Lights Out

## Izbira strežnika
Za strežnik sem uporabil Quarkus, za bazo pa Postgres.

Uporabljal sem le "quarkus dev", ki aplikacijo zažene v development mode. V development mode(s predpostavko, da je Docker zagnan),
se datasource za ORM sam ustvari.
Lahko pa tudi aplikacijo zapakiramo z "mvn package" in zaženemo z "java -jar target/quarkus-app/quarkus-run.jar",
a je prej potrebno nastaviti datasource v application.properties.


## Solver Algoritem

Na kratko bom opisal delovanje algoritma. Žal markdown ne podpira matematičnih izrazov, tako da bo opis manj natančen.

### Preslikanje Lights Out problema, na sistem enačb

Najprej je potrebno opaziti, da zaporedje klikov nima vpliva na rešitev, ter da je v rešitvi vsako polje le kliknjeno, ali ne, saj
je trikratni klik je enakovreden enkratnemu kliku, dvokratni nobenemu...

Kot rešitev torej iščemo za vsako polje, ali mora biti kliknjeno. 
Končna vrednost nekega polja, je odvisna le od sosednjih polj, polja samega, ter njegove začetne vrednosti.
Končna vrednost je zato enaka ostanku pri deljenju z 2 seštevka: klikov sosednjih polj(vsako polje le 0 ali 1)
polja samega in začetne vrednosti polja.

Fiksiranje končne vrednosti polj na 1 nam porodi sistem n^2 enačb z n^2 neznankami, ki deluje v modulo 2.


### Reševanje sistema

Reševanje sistema je enostavno. Sistem spravimo v zgronje trikotno matriko in nato upoabimo obratno substitucijo.
Ker sistem deluje v modulo 2, je spravljanje na zgornje trikotno matriko izjemno enostavno, saj se ukvarjamo le z 1 in 0.
Pri določenih n-jih, matrika sistema ni obrnljiva. Pri takih sistemih, se zato lahko zgodi, da dobimo kakšno vrstico,
ki pravi "0=1". V takih primerih, sistem rešitve nima. V ostalih primerih, pa nam vrstice "0=0" dodajo proste parametre rešitve.
Za iskanje optimalne rešitve zato proste parametre fiksiramo na različne vrednosti in tako pridobimo različne rešitve.
Vzamemo tisto z najmanj enicami, saj enice predstavljajo klike.


### Pospešitve Algoritma

V moji implementaciji algoritma popolnoma zanemarjamo obliko dane matrike. Matrika je 5-diagonalna. Zato je ne bi bilo potrebno
shranjevati v nxn tabelo. Prav tako se da to lastnost izkoristiti pri gaussovi eliminaciji (ni treba, čez cel stolpec,
da smo sigurni, da so na dnu samo 0). Najbrž bi podobno veljalo tudi pri obratni substituciji, a tako daleč nisem razmislil.

Kakorkoli že, zgornje bi imelo velik pomen šele pri večjih n-jih. Za matrike od 3x3 do 8x8 algoritem dela odlično.

## Testiranje

Testiral sem le na nivoju service-ov in ne samih endpointov. 
Testi se lahko zaženejo s komando "mvn test".
Eden izmed testnih razredov ne izvaja pravih testov, saj poskuša le hitrost algoritma, tako da reši po 100000 problemov
velikosti n, za vse n od 3-8, ter izpiše svojo hitrost.
Pustil sem ga noter, saj nisem 100%, da mi bo uspelo v logih predstavit hitrost.

## Logging
Žal mi je za loganje zmanjkalo časa, tako da so edini logi, ki jih "ročno" pišem tisti, od solverja.
Log o solverjevi hitrosti se izpiše kot:
INFO  [lig.ser.ProblemsService] (executor-thread-0) The problem was solved in 1.86609 ms with 3 steps.

## Openapi
Swagger UI je (pri zagonu s "quarkus dev" komando, v nastavitvah se da nastaviti, da je zmeraj) dostopen na http://localhost:8080/q/swagger-ui/

Openapi definicija pa na: http://localhost:8080/q/openapi