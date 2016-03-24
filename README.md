
<html>
  <head>
  </head>
  <body>
    <h1><span class="mw-headline">Úloha 4 </span></h1>
    <p><b>Název</b>: Kompletní implementace aplikační vrstvy + testy<br><b>Počet bodů</b>: 3<br><b>Způsob řešení</b>: Úlohu řeší oba členové týmu dohromady.</p>
    
    <h3>Potreba udělat</h3>

<h4>Implementace</h4>
<ol>
<li>Agency: 0%</li>
<li>Agent: 0%</li>
<li>Mission: 0%</li>
</ol>

<h4>Testy</h4>
<ol>
<li>Agency: 0%</li>
<li>Agent: 0%</li>
<li>Mission: 0%</li>
</ol>
    <h2><span class="mw-headline">Zadání </span></h2>
    <ol>
      <li>Kompletně implementujte celou aplikační vrstvu a vytvořte testy pro všechny metody.</li>
      <li>Můžete si vybrat, zda použijete třídu <a href="https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html"><code>JdbcTemplate</code></a> z modulu <a href="https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/jdbc.html">spring-jdbc</a>, <a href="https://commons.apache.org/proper/commons-dbutils/apidocs/org/apache/commons/dbutils/QueryRunner.html"><code>QueryRunner</code></a> z <a href="https://commons.apache.org/proper/commons-dbutils/">commons-dbutils</a>, nebo zda si vystačíte s čistým JDBC.&nbsp;</li>
      <li>Třídy implementující aplikační logiku implementujte tak, aby byly vláknově bezpečné (tj. aby více souběžných operací nesdílelo stejnou instanci <a href="https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html"><code>java.sql.Connection</code></a>). Pro získání připojení k databázi použijte výhradně <a href="https://docs.oracle.com/javase/8/docs/api/javax/sql/DataSource.html"><code>DataSource</code></a>.</li>
      <li>K aplikaci nezapomeňte přibalit SQL skript pro založení potřebných tabulek v databázi.</li> </ol>
    <h2><span class="mw-headline">Tipy </span></h2>
    <ul>
      <li>Vzorový příklad je k dispozici v git repozitory&nbsp;<a href="https://github.com/petradamek/PV168">https://github.com/petradamek/PV168</a>&nbsp;v adresáři&nbsp;<a href="https://github.com/petradamek/PV168/tree/master/GraveManager-SimpleDBImpl">GraveManager-</a><a href="https://github.com/petradamek/PV168/tree/master/GraveManager-Backend">Backend</a>. Obsah celé repozitory si můžete stáhnout příkazem&nbsp; <div style="background:#eee;border:1px solid #ccc;padding:5px 10px;"><code>git clone https://github.com/petradamek/PV168.git</code></div> Příklad průběžně upravujeme a doplňujeme, tak si ho občas zaktualizujte <div style="background:#eee;border:1px solid #ccc;padding:5px 10px;"><code>git pull</code></div> </li>
      <li>Jako DataSource doporučujeme použít&nbsp;<code><a href="https://db.apache.org/derby/docs/10.12/publishedapi/org/apache/derby/jdbc/EmbeddedDataSource.html">org.apache.derby.jdbc.EmbeddedDataSource</a></code>&nbsp;(viz např. metoda prepareDataSource() ve třídě&nbsp;<a href="https://github.com/petradamek/PV168/blob/master/GraveManager-Backend/src/test/java/cz/muni/fi/pv168/gravemanager/backend/GraveManagerImplTest.java"><code>GraveManagerImplTest</code></a>). Alternativně můžete použít&nbsp;i knihovnu <a href="http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html">Tomcat DBCP</a> nebo její starší verzi&nbsp;<a class="external text" href="http://commons.apache.org/dbcp/" rel="nofollow" title="http://commons.apache.org/dbcp/">Commons DBCP</a>.</li>
      <li>Pro práci s datem a časem doporučujeme používat třídy z balíku <code><a href="https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html">java.time</a></code>, například <a href="https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html"><code>LocalDate</code></a>, <a href="https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html"><code>LocalTime</code></a> nebo <code><a href="https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html">LocalDateTime</a></code>&nbsp;(viz např. <a href="https://github.com/petradamek/PV168/blob/master/GraveManager-Backend/src/main/java/cz/muni/fi/pv168/gravemanager/backend/Body.java"><code>Body</code></a> a&nbsp;<a href="https://github.com/petradamek/PV168/blob/master/GraveManager-Backend/src/main/java/cz/muni/fi/pv168/gravemanager/backend/BodyManagerImpl.java"><code>BodyManagerImpl</code></a>). Pokud byste používali&nbsp;<code class="plainlinks"><a class="external text" href="http://java.sun.com/javase/6/docs/api/java/util/Date.html" rel="nofollow" title="http://java.sun.com/javase/6/docs/api/java/util/Date.html">java.util.Date</a></code> a <code class="plainlinks"><a class="external text" href="http://java.sun.com/javase/6/docs/api/java/sql/Timestamp.html" rel="nofollow" title="http://java.sun.com/javase/6/docs/api/java/sql/Timestamp.html">java.sql.Timestamp</a></code>, dejte si pozor na kombinování těchto hodnot ve stejné kolekci.&nbsp;Metoda <code class="plainlinks"><a class="external text" href="http://java.sun.com/javase/6/docs/api/java/sql/Timestamp.html#equals(java.lang.Object)" rel="nofollow" title="http://java.sun.com/javase/6/docs/api/java/sql/Timestamp.html#equals(java.lang.Object)">Timestamp.equals(java.lang.Object)</a></code> nedodržuje obecný kontrakt metody <code class="plainlinks"><a class="external text" href="http://java.sun.com/javase/6/docs/api/java/lang/Object.html#equals(java.lang.Object)" rel="nofollow" title="http://java.sun.com/javase/6/docs/api/java/lang/Object.html#equals(java.lang.Object)">Object.equals(java.lang.Object)</a></code> a není symetrická — při porovnávání s hodnotou typu <code class="plainlinks"><a class="external text" href="http://java.sun.com/javase/6/docs/api/java/util/Date.html" rel="nofollow" title="http://java.sun.com/javase/6/docs/api/java/util/Date.html">Date</a></code> vrací vždy false, i když se jedná o stejný čas. Vysvětlení je uvedeno v dokumentaci třídy <code class="plainlinks"><a class="external text" href="http://java.sun.com/javase/6/docs/api/java/sql/Timestamp.html" rel="nofollow" title="http://java.sun.com/javase/6/docs/api/java/sql/Timestamp.html">java.sql.Timestamp</a></code>.</li>
      <li>Pokud potřebujete pracovat s aktuálním datem a časem, nezapomeňte zajistit, aby testy zůstaly deterministické.&nbsp;Jako zdroj aktuálního data a času&nbsp;použijte instanci třídy <a href="https://docs.oracle.com/javase/8/docs/api/java/time/Clock.html"><code>Clock</code></a>, kterou do příslušné komponenty nainjektujete v konstruktoru.&nbsp;V testech pak použijte mock instanci třídy Clock, která bude vracet předem definovaný čas (viz např. commit&nbsp;<a href="https://github.com/petradamek/PV168/commit/647cabc577bb8d11eaff0c80b435b15b600bdce7">647cabc5</a> ve vzorovém příkladu).&nbsp;</li>
      <li>Pokud používáte ruční řízení transakcí, nezapomeňte vždy po získání instance&nbsp;<a class="external text" href="http://java.sun.com/javase/6/docs/api/java/sql/Connection.html" rel="nofollow" style="font-family: monospace; " title="http://java.sun.com/javase/6/docs/api/java/sql/Timestamp.html">java.sql.Connection</a>&nbsp;z&nbsp;<a class="external text" href="http://java.sun.com/javase/6/docs/api/javax/sql/DataSource.html" rel="nofollow" style="font-family: monospace; " title="http://java.sun.com/javase/6/docs/api/java/sql/Timestamp.html">javax.sql.DataSource</a>&nbsp;vypnout režim autocommit a před vrácením (tj. uzavřením) spojení režim autocommit opět zapnout.&nbsp;</li> </ul>
  </body>
</html>
