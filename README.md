# DIP107P 1. mājasdarbs "Šķirošanas algoritmi"

Mājasdarba ietvaros bija jāpēta vismaz 6 šķirošanas algoritmi un jāveic to
salīdzināšana.

# Metodoloģija

Visi algoritmi veic kārtošanu pār veselo skaitļu masīvu, `int[]`. Realizēti šādi
algoritmi:

- [Tiešās iekļaušanas metode][alg-insertion]
- [Šeikera metode][alg-shaker] daļēji pēc (Wirth, 1976)
- [Apvienošanas metode][alg-merge] pēc (Wirth, 1976)
- [Hoara metode][alg-quick]
- [Paralēlā apvienošanas metode][alg-parallel]
- [Daļēji paralelizētā paraugņemšanas metode][alg-sample] kā Hoara metodes
  vispārināta paralelizācija

[alg-insertion]: ./InsertionSort.java
[alg-shaker]: ./CocktailShakerSort.java
[alg-merge]: ./MergeSort.java
[alg-quick]: ./Quicksort.java
[alg-parallel]: ./ParallelMergeSort.java
[alg-sample]: ./Samplesort.java

## Darbināšana

Datu iegūšanai JVM tika darbināts gan noklusējuma režīmā, gan ar atspējotu [JIT
kompilatoru][jvm-jit]. Vairums Java sistēmu izmanto JIT kompilatoru, tādēļ
pirmais režīms dod rezultātus, kas ir tuvi realitātei, taču mazāk paredzami, jo
JIT process inherenti nav caurspīdīgs; tādēļ otrajā režīmā rezultāti ir sliktāki
patērētā laika ziņā, taču paredzamāki un salīdzināmāki.

[jvm-jit]: http://cr.openjdk.java.net/~vlivanov/talks/2015_JIT_Overview.pdf

## Paralēlās metodes

Kā novitāte ārpus uzdevuma nosacījumiem tiek piedāvāta paralēlā kārtošana,
realizēta ar nolūku paaugstināt kārtošanas veiktspēju liela izmēra masīviem uz
vairākprocesoru sistēmām. Tā kā Java vide ir inherenti piemērota paralēlai
skaitļošanai ar pavedieniem, [paralēlā apvienošanas metode][alg-parallel] un
[daļēji paralelizētā paraugņemšanas metode][alg-sample] sadala kārtojamo masīvu
vairākos apgabalos un katru apgabalu kārto uz atsevišķa pavediena (tātad arī uz
atsevišķa loģiskā procesora). Galvenā atšķirība ir tajā, kā notiek masīva
dalīšana apgabalos. Aprakstos `N` atbilst paralelizācijas pakāpei, kas sakrīt ar
sistēmā pieejamo loģisko procesoru skaitu.

- Paralēlā apvienošanas metode sadala masīvu `N` vienādos apgabalos bez to
  vērtību priekšlaicīgas pārbaudes, paralēli sakārto apgabalus katru atsevišķi,
  pēc tam veic apvienošanu (līdzīgi apvienošanas metodei) starp visiem
  apgabaliem vienlaicīgi, iegūstot sakārtotu masīvu.
- Paraugņemšanas metode pirms sadales atlasa `N - 1` paraugus¹ un izveido jaunus
  `N` apgabalus, un uz tiem no masīva pārkopē elementus, atbilstoši tam, starp
  kuriem diviem paraugiem dotais elements atrodas, tad sakārto visus apgabalus.
  Apgabali atmiņā tiek izkārtoti secīgi (vienā masīvā), kā arī paraugi pirms
  lietošanas tiek sakārtoti, tātad gala masīvs pēc paralēlās daļas nav atsevišķi
  jākārto vai jāmaina.

¹ Realitātē paraugu ņemšana tiek [četrkāršota][alg-sample-oversampling], lai
iegūtie paraugi labāk atspoguļotu ievaddatu sadalījumu un apgabali būtu līdzīga
izmēra.

[alg-sample-oversampling]: ./SampleSort.java#L63-90

Abas divas metodes uz katra kodola šķirošanu veic ar brīvi izvēlētu algoritmu no
iepriekšminētajiem. Pētīšanā abas paralelizācijas metodes tika pārbaudītas ar
tiešās iekļaušanas metodi un apvienošanas metodi, lai būtu iespējams gan tās
salīdzināt savā starpā, gan arī neparalēlos algoritmus salīdzināt ar
paralēlajiem.

# Analīze

Atbilstoši literatūrai, tika novērots, ka atsevišķas metodes (tiešās
iekļaušanas, šeikera) darbojas ātrāk uz maziem masīviem, taču to veiktspēja
strauji samazinās, pieaugot masīva izmēram; kā arī, ka citas metodes (Hoara,
apvienošanas) maziem masīviem darbojas lēnāk, taču to veiktspējas samazināšanās
pie ievaddatu apjoma pieauguma ir lēnāka.

Līdzīgi, kārtošanas paralelizācija sniedz veiktspējas uzlabojumu, taču tai arī
ir fiksēti virstēriņi, kā rezultātā nelielu masīvu kārtošana paralēli
viennozīmīgi ir lēnāka par gan vienkāršajām, gan optimizētajām neparalēlajām
metodēm.

Empīriski tika novērots, ka paraugņemšanas metode nenodrošina optimālu
veiktspēju. Tā kā paraugi tiek izvēlēti nejauši no ievades datiem, pat pēc to
normalizēšanas tie nereprezentē vienmērīgu datu sadalījumu, un tādēļ dalītie
apgabali ir nevienmērīga izmēra, kā rezultātā darbs starp procesoriem tiek
sadalīts nevienmērīgi. Paralēlā apvienošanas metode šajā ziņā darbojas labāk.

# Licence

[BSD 2 pantu](./LICENSE)

# Atsauces

WIRTH, Niklaus, 1976. _Algorithms + Data Structures = Programs._ Englewood
Cliffs: Prentice-Hall. ISBN 0-13-022418-9.
