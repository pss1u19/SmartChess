package karballo.hash

import karballo.Board
import karballo.Square
import karballo.bitboard.BitboardUtils

/*
 * Computes the Polyglot key of a board
 */
object ZobristKey {

    val exclusionKey = 6085104687737119477

    val pawn = arrayOf(longArrayOf(8767779863385330152, -9058528327592164873, 7168173152291242201, 1872523786854905556, 8920438500019044156, 5806056339682094430, 575228772519342402, 6004979459829542343, -1613020756858095303, -6585158539226940220, 928423270205194457, -4940966502553021718, 2649182342564336630, -2684379813869300996, -1061241206578699883, 1489771135892281206, -452438870359771710, 8118948727165493749, -7312248682904753503, -1255011998991573177, 8264435384771931435, 6103491276194240627, -8847901732119490575, 2875176145464482879, -8221078497326742194, -69816450156784432, 658149708018956377, -8665204105580500247, 1810658488341658557, -5887731630549795598, -6672393216155760456, -8668335600576165967, 7135057069693417668, 5728850993485642500, -7317033582308389709, 2166287019647928708, 6628917895947053289, 4111328737826509899, -5480269155866560275, -7199510714699703159, 8877430296282562796, -1183745501610368782, -2318345334258142041, 7278670237115156036, 1765885809474863574, 5794634036844991298, 6600979542443030540, 2409960466139986440, -1304134592068362083, 4357691132969283455, -776761410179450844, -3511034280684146806, 5365205568318698487, -5365866959392798091, 407550088776850295, -5044746123895283133, 8332101422058904765, 8136607301146677452, -7922319286853618274, 306186389089741728, -6753601202686884558, -7505469452821431437, -8767545796439405940, 8763584794241613617), longArrayOf(-1712897760329768758, 1133690081497064057, 3643601464190828991, 8467686926187236489, -7199576581966697758, 4961560858198160711, 3096006490854172103, -7117617611634414271, 2566923340161161676, 6752165915987794405, 77499164859392917, 4665150172008230450, -3449195065476763947, -7636462362570079312, 1875810966947487789, 972344712846728208, 5454859592240567363, 3680699783482293222, -927302695338870676, -9023119502491076660, 9012210492721573360, 8379435287740149003, 7466832458773678449, 419294011261754017, 2642043227856860416, -3943696798190020515, 8864492181390654148, -5428191116858910017, 1870057424550439649, -8659851112597712361, -8233256716529052661, -6168633590159683332, 8313129892476901923, 3852097769995099451, 707553987122498196, 7168370738516443200, 236592455471957263, -6405355056885088877, 1488034881489406017, 5521189128215049287, -218426187369631167, 6666107027411611898, 5903053950100844597, -6956705385196247004, -4640889180432530876, -3237903676947602091, 2291114854896829159, 1761594034649645067, -7577707393933148579, 5611679697977124792, 7307368198934274627, 1284939680052264319, 9148094160140652064, -5063052553617656857, -8325814959521189771, 3626425922614869470, -1434736733280752266, 7195685255425235832, -7798627118210467701, 4177248038373896072, 6849775302623042486, 4794093907474700625, 7866624006794876513, 4819485793530191663))
    val rook = arrayOf(longArrayOf(-3346872395976619286, 2088698177441502777, -3882604781526113051, -5297324270288368904, -2311998167136774173, -2974960788477327719, -396816797984991405, -6872901447497050402, 8756104692490586069, 2993471197969887147, 2774412133178445207, 8308552077872645018, 6019498834983443029, 1684901764271550206, -1955705304021469742, 5679318949880730421, 2746718911238191773, 2421696223438995901, 7400684525794093602, 5531291136777113635, 5816122712455824169, -1318335436663551995, -7054918957238328127, 7404378077533100169, 3960368502933186560, 4086544267540179726, -795500665323736509, -3513011632246703702, 2151598180055853022, -5091566080359364152, -2931757618997826117, 2297623779240041360, -1830170615095512051, -3155757358848065085, -805786339615115179, -8392284860076226467, 2706199935239343179, -5833233003560719734, -7184131849074362877, -1498129122236047154, 795178308456769763, 3240842176865726111, 1255659969011027721, -6512755018830767576, 7167044992416702836, -6102548667584133652, -4421998591500243275, 2626432152093131908, -9204136428534629549, -2933041310131459385, 8937438391818961808, 1486633608756523830, 3362936192376505047, -4114748024493078816, 6408553047871587981, -6057660688470347791, 1344590668019013826, -3149974553771293647, -5432401932016084426, 6576914768872977647, -6779834932302576312, 5294122026845313316, 225631029947824688, -1447368335367659065), longArrayOf(1215083005313393810, -5460400459106163223, 4869881251581666789, -6084204670034616524, -3573548994530823287, 3928039610514994951, -2531729836700005339, -2721803534724933711, -5834172998942348995, -6047495272998113561, 2305696533800337221, -6264054769160028483, -4625529213342012336, -5297507950653649788, -7260687268226227326, -2611389914965073217, 7915171836405846582, 7437568954088789707, -6934285729554595366, -884756771763845121, -4347701924302501399, -4360815174901894470, -5379009770048591566, 1949121388445288260, 8376971840073549054, -7122885458123533268, 5609875541891257226, 8412807604418121470, -2192588427498456587, -8462680832637173339, 520574178807700830, -6694092778555253967, -7715852896319476306, -2909286158269631396, 8271005071015654673, -8105325240265828673, 2167677454434536938, 5204145074798490767, -2972521237957530560, 1383314287233606303, -2492284066326686611, 7334775646005305872, 3447475526873961356, 5895970210948560438, -7125392814103915483, -6607626754690151490, -2420506449658262810, -6935247590537980960, 4220875528993937793, 3197166419597805379, 2204997376562282123, 8524679326357320614, -6141772829141909856, 1912937584935571784, 8334626163711782046, 6939292427400212706, -8077052725099091274, -8573836118959825828, -7207128850928187954, 9186432380899140933, 4570145336765972565, -4001944857184464756, 5085503808422584221, 2803169229572255230))
    val knight = arrayOf(longArrayOf(4303848835390748061, -2369562330250108912, -7780501039097782411, -7848113977168848178, 629718674134230549, -100692273235294726, 8710936142583354014, -4231143402258348978, 5704473791139820979, -6704140522967532107, -6395030266941625231, 3706272247737428199, -2784376253081124953, -2885415085016290431, -602825012910263304, -3262948163554511041, 5477894166363593411, 1800584982121391508, -238877964596788188, 4157033003383749538, -889891063729036404, 8908762506463270222, 1637612482787097121, -8659436106485368743, 3876110682321388426, 3605513501649795505, 4039979115090434978, -7125432498641740945, 8799727354050345442, 7550910419722901151, -8286718691916758335, 4674885479076762381, -7125263955721355405, 5957016279979211145, 5949110547793674363, -2791760472357952421, 4855373848161890066, 5854220346205463345, 8620494007958685373, -6265874557923512399, 2903841526205938350, 6268205602454985292, 925952168551929496, 6489982145962516640, 297395810205168342, 8801329274201873314, 2094843038752308887, 8228060533160592200, 6778454470502372484, 1070089889651807102, -5757784274115990919, 5795683315677088036, -8639167831183607326, -7307597380444705476, 8371662176944962179, 359914117944187339, 5245566602671237210, -7356718043541534927, -7799067531746373637, -6312457544560218087, 942692161281275413, -2685835992369688543, 1983224895012736197, -686688449954468754), longArrayOf(-2509529976529168132, -5881067973083878800, -43044781523771743, -7849913836115364766, 5040091220514117480, -4962589000476002385, -1176482456577273983, 6215931344642484877, -6154561019552412806, 4851386166840481282, 2012514900658959106, -2394674665211165194, -9184011107927260315, -3956411269506295511, 2521545561146285852, 9145986266726084057, 2931510483461322717, 1211454228928495690, -7946502699748727984, -5470303936463679940, 2445601317840807269, -2390612934246005514, 7319524202191393198, -6307235393847693738, -5840131557960057277, 5189293263797206570, -5827584294354409384, -1211743989268045124, -7222186592991335468, 3253094721785420467, 6217490319246239553, -4193944676823227306, 5603848033623546396, 463713201815588887, -7087500065266820785, -4835227570594987755, 1240625309976189683, -797006402233243920, 5933835573330569280, 1850950425290819967, -606925578055940448, 8519766042450553085, 3310549754053175887, 2422944928445377056, -5511954857782272889, -5445157863262884228, -4577276249006689100, 5837679654670194627, -7286398923439835928, -5738531550834291303, -3475573908164538853, 5210919022407409764, 3197637623672940211, 862037678550916899, -2435486243585145781, -2942946220820427471, 7423022476929853822, -8157851634567385392, -7176401658345012201, -258250171718674949, -453254132140704618, 7618258446600650238, -1777488582077034890, -6558283579219683126))
    val bishop = arrayOf(longArrayOf(3451078315256158032, -1668041885421938881, -5369083948807480889, 392580029432520891, -6280299527312203635, 5122261027125484554, -4381758060701354339, 2573542046251205823, 5275192813757817777, -550798907951659849, -4723074364987629396, 4479827962372740717, -7615990091925507577, 1667962162104261376, 9205113463181886956, 1238907336018749328, -1635869182558457729, -6165228101000850014, 4313244667902787366, -3689732299588779379, 1776293542351822525, -253162723436299526, 2290795724393258885, 2148246364622112874, -2611856101870623399, -7754480333218511484, 7248312053715383136, -5061732229000748930, 3305807524324674332, 3321611548688927336, -3108706094173697875, -5214973253231643794, -3459299730036712668, 8762207035762213579, -8842837798196294764, 8896660382367628050, -7494927886966539228, 7848957776938116907, 9112428691881135949, -3282213444060272849, -6921029952340425425, -6961025044515806144, 685071415365890925, 6373588016705149671, -4145362416552097252, 7197363620976276483, -7855717824450087951, -9036992843571423785, 8824404812019774483, 9174156641096830119, 1303007271453773655, 8880818733894805775, 6144308296388004591, 6251124536988276734, -1733165894704959795, -1983292083700476020, 9195534799547011879, 6704191827946442961, 4306406647446967767, 5595889224692918441, -7709859765033275901, 8883975763174874978, -8495187234923475788, 4420129794615782201), longArrayOf(5773445318897257735, -8034650149685246498, 1909608352012281665, 928484295759785709, -3214461869429917290, 3229483537647869491, 6368791473535302177, 9195060651485531055, 3913469721920333102, 7798983577523272147, -485352023391264328, 616435239304311520, -3128590709331025083, 4209783265310087306, -7675348306896289970, 3990834569972524777, -5529078456224335278, -4987831551100114613, -1390141990708018827, 6221161860315361832, -1217848141703766125, 1654244791516730287, 6239239264182308800, -3022076089717407198, -7415446863621302972, 6465331256500611624, -4881735209222593326, 8796640079689568245, 510457344639386445, -5466800325109811545, -5424555790927851038, 7337288846716161725, -4045937359548092780, 8924250025456295612, -2849693101024154289, 3527832623857636372, 6222903725779446311, -3394528423913180349, 3627975979343775636, 8017026739316632057, 3881319844097050799, -5751042123502621052, -5480222307947335495, 1140009269240963018, -2764893807176054556, 4223238849618215370, -3978586660626014369, -7747633557851937220, -4906602186354729269, -3655428046510026134, -4166857726643058241, 7580967567056532817, -5067872407110470413, -7902221177050684800, -1442565630059000999, -1588072837735502258, 687173692492309888, -9149066151885549738, -8849761751120126775, 704784010218719535, -1587952015247572959, -6648276097457692213, -5005447323036875848, -2556870867600940496))
    val queen = arrayOf(longArrayOf(8217932366665821866, 2028126038944990910, -2078162528421891077, 612060626599877907, 638838107884199779, 7264555371116116147, -2451832056240883254, 8070015023023620019, -7802992490642642440, -1709513839274943977, 6461588461223219363, 7353589116749496814, -2876688578317531702, 4689038209317479950, 4917760284400488853, -5731027174718830117, -5376351730844657950, -1356161753273905001, 5344020438314994897, -2865201508933622433, 5795026310427216669, 6280119077769544053, -3822959266735082868, -4557372729334641201, 8954801150602803298, -6507191444373290905, 7696730671131205892, 7802414647854227001, -7759862821659811914, -7283548499500808528, 5973851566933180981, 2499216570383001617, 5766400084981923062, -2446429154351403408, -3323431321145327255, 7716832357345836839, 7710978612650642680, 4997389530575201269, -7838261593662321029, 5805966293032425995, 7493701010274154640, 3568641929344250749, 4208705969817343911, 3651028258442904531, -1254265329846611475, 7910921931260759656, 8713884558928322285, -7200857806063814540, 7780701379940603769, 3972025232192455038, 3841784002685309084, -339391230761074478, -847676012271350765, -6156110632223716108, -5287726615758083227, 2245920858524015772, -5353646232683726234, -8099574507787307615, 639100052003347099, -236792732567011685, 331717439817162336, -6948684187833482934, -2078634282757881654, -3673543119483549832), longArrayOf(2006921612949215342, -3297604595876809216, 2679459049130362043, -5523242177286330794, -7404447858617298160, -6735912242669201170, 1763076921063072981, 8870296219354404, 2599246507224983677, 7262306400971602773, -3862070249752561130, 4067155115498534723, -478226388169132300, -8280748781830503314, 4254066403785111886, 2441159149980359103, -5127046408239983365, 2141838636388669305, 5749101480176103715, 7158260433795827572, 452576500022594089, 8684305384778066392, 9203696637336472112, 1183331494191622178, -7426305334632631762, 8127707564878445808, -5310895318150965567, -6221075131749871973, 3345333136360207999, -1158177344673395093, -2742143461777474807, -6707005227519968031, -250124276606665209, 2597087673064131360, -4643966542147613368, 7043015398453076736, -5105491203086766093, 4467639418469323241, -7727291720446440205, -4646510815755199649, 8471065344236531211, 1975862676241125979, 7047939442312345917, 2826050093225892884, -3198758860386093361, -9073093575002869048, -6843171236372059126, -1224189853575564238, 8752186474456668498, 1184291664448112016, -8317869669453183656, -213906536485350214, 8891398163252015007, 6983092299355911633, -5736484889461131955, -7665310745516932263, 1895531807983368793, 6743071165850287963, -3239204636105726481, -7100384126557577363, -7052086191139373095, -5415306440973393561, 8304258407043758711, -6562869240740929461))
    val king = arrayOf(longArrayOf(2378655170733740360, -1693429024984254962, 3534397173597697283, -5332828906549230440, -2668126031979123873, -3836896475970613836, -5160524448685921952, 6176181444192939950, 319261663834750185, -7626055490284413820, -741773765110731084, 1942137629605578202, -435572429638872008, -4269103759267730770, 551298419243755034, -552643018769440755, 5215882904155809664, 2462272376968205830, 3154220012138640370, 1692791583615940497, 5921710089078452638, -7192578181343322179, -7617977520976348028, -1126723894144361908, -3060551081441224714, 2535530668932196013, 2648366387851958704, -7088568204036607476, -1956459363404282278, 2282028593076952567, -8434249029387572864, 9063345109742779520, -7796891254993561507, 2131804225590070214, 1724859165393741376, -7040231872174702793, -2912572593751847674, 744965241806492274, 9131737009282615627, 6797681746413993003, -4171543124651995508, 2847738978322528776, 8982836549816060296, -3589502756575571236, 6426639016335384064, -4416447665223034257, -3028133809084889899, 7348272751505534329, 8210570252116953863, -1818291546970408658, 764541931096396549, 879136823698237927, 1454069630547688509, 7252270709068430025, -6959678130640022028, 1517491100508351526, -3310835631931791777, -7962612811332817823, 6239858431661771035, 6892625624787444041, -9105903926093857371, -7567181820563274204, -1789924905178677132, -1027731306262305510), longArrayOf(-2976794435908412034, -2068809033829413525, 6034935405124924712, 2008219703699744969, 2415982786774940751, 4217798054095379555, 4894708394808468861, 2526013881820679475, -3114493420313727393, -6994674436138682061, 1218420902424652599, 1011542511767058351, 4587441767303016857, -5689255409585681882, 3171782229498906237, 6813989660423069229, -5432657379715846560, -6051660936535374862, -8519877247653478975, -5363013951754450589, 1805505087651779950, -1404709700660885128, -7947387725428979194, -9127831760372958176, 6332958748006341455, 756689505943647349, -1781398766969753407, 2644524053331857687, -1727796887562541994, 4275128525646469625, 4496402702769466389, -354324339393897847, 1895999114042080393, -7418949222395686301, -8911359377770791788, 550475751710050266, -8488570490783378132, -3321886456795945333, 3282372277507744968, 5397518675852254155, 5069314540135811628, 1867780161745570575, 5664728055166256274, -3680555303400859359, 5100888107877796098, -8275867100986890362, 3512907883609256988, -650773357961068032, -6071470395341666208, -7470369288476554443, 3830179243734057519, -4037852901788685727, 248384571951887537, 6489498281288268568, 8428576418859462269, -7620386572563399119, -47443777466463686, 5641726496814939044, 8287918193617750527, 1418237564682130775, -2744615621445586530, 8812437177215009958, 5317296011783481118, -3529173984278120528))
    val whiteKingSideCastling = 3591372000141165328
    val whiteQueenSideCastling = -1052235342745599600
    val blackKingSideCastling = -6521666110210903136
    val blackQueenSideCastling = 2231224496660291273
    val passantFile = longArrayOf(8127998803539291684, -2154291592623801641, 16488107566197090, 2060923303336906913, -3516953014032317815, -3394515125949629582, 8630622898638529667, 7467898009369859339)
    val whiteMove = -516182592762444535

    fun getKeyPieceIndex(index: Int, pieceChar: Char): Long {
        when (pieceChar) {
            'P' -> return pawn[0][index]
            'p' -> return pawn[1][index]
            'R' -> return rook[0][index]
            'r' -> return rook[1][index]
            'N' -> return knight[0][index]
            'n' -> return knight[1][index]
            'B' -> return bishop[0][index]
            'b' -> return bishop[1][index]
            'Q' -> return queen[0][index]
            'q' -> return queen[1][index]
            'K' -> return king[0][index]
            'k' -> return king[1][index]
        }
        return 0
    }

    fun getKey(board: Board): LongArray {
        val key = longArrayOf(0, 0)

        var square = Square.H1
        var index: Byte = 0
        var color: Int
        while (square != 0L) {
            color = if (square and board.whites != 0L) 0 else 1
            key[color] = key[color] xor getKeyPieceIndex(index.toInt(), board.getPieceAt(square))
            square = square shl 1
            index++
        }

        if (board.whiteKingsideCastling) key[0] = key[0] xor whiteKingSideCastling
        if (board.whiteQueensideCastling) key[0] = key[0] xor whiteQueenSideCastling
        if (board.blackKingsideCastling) key[1] = key[1] xor blackKingSideCastling
        if (board.blackQueensideCastling) key[1] = key[1] xor blackQueenSideCastling
        // passant flags only when pawn can capture
        val passant = board.passantSquare
        if (passant != 0L && (!board.turn && passant shl 9 or (passant shl 7) and board.blacks and board.pawns != 0L
                || board.turn && passant.ushr(9) or passant.ushr(7) and board.whites and board.pawns != 0L)) {
            color = if (board.turn) 0 else 1
            key[1 - color] = key[1 - color] xor passantFile[BitboardUtils.getFile(passant)]
        }
        if (board.turn) key[0] = key[0] xor whiteMove
        return key
    }
}