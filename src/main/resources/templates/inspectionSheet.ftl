<?xml version="1.0"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:html="http://www.w3.org/TR/REC-html40">
 <DocumentProperties xmlns="urn:schemas-microsoft-com:office:office">
  <Author>Bear_Xiong</Author>
  <LastAuthor>Bear_Xiong</LastAuthor>
  <Created>2015-06-05T18:19:34Z</Created>
  <LastSaved>2019-11-08T08:03:26Z</LastSaved>
  <Version>16.00</Version>
 </DocumentProperties>
 <OfficeDocumentSettings xmlns="urn:schemas-microsoft-com:office:office">
  <AllowPNG/>
 </OfficeDocumentSettings>
 <ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel">
  <WindowHeight>12645</WindowHeight>
  <WindowWidth>22260</WindowWidth>
  <WindowTopX>32767</WindowTopX>
  <WindowTopY>32767</WindowTopY>
  <ProtectStructure>False</ProtectStructure>
  <ProtectWindows>False</ProtectWindows>
 </ExcelWorkbook>
 <Styles>
  <Style ss:ID="Default" ss:Name="Normal">
   <Alignment ss:Vertical="Bottom"/>
   <Borders/>
   <Font ss:FontName="等线" x:CharSet="134" ss:Size="11" ss:Color="#000000"/>
   <Interior/>
   <NumberFormat/>
   <Protection/>
  </Style>
  <Style ss:ID="s63">
   <Alignment ss:Vertical="Bottom" ss:WrapText="1"/>
   <NumberFormat ss:Format="@"/>
  </Style>
  <Style ss:ID="s73">
   <Alignment ss:Horizontal="Center" ss:Vertical="Bottom" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <NumberFormat ss:Format="@"/>
  </Style>
  <Style ss:ID="s74">
   <Alignment ss:Horizontal="Center" ss:Vertical="Bottom" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <NumberFormat ss:Format="@"/>
  </Style>
  <Style ss:ID="s75">
   <Alignment ss:Horizontal="Left" ss:Vertical="Center" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <NumberFormat ss:Format="@"/>
  </Style>
  <Style ss:ID="s76">
   <Alignment ss:Vertical="Bottom" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <NumberFormat ss:Format="@"/>
  </Style>
 </Styles>
 <Worksheet ss:Name="Sheet1">
  <Table ss:ExpandedColumnCount="8"  x:FullColumns="1"
   x:FullRows="1" ss:StyleID="s63" ss:DefaultColumnWidth="54">
   <Column ss:StyleID="s63" ss:AutoFitWidth="0" ss:Width="96"/>
   <Column ss:StyleID="s63" ss:AutoFitWidth="0" ss:Width="142.5" ss:Span="2"/>
   <Column ss:Index="5" ss:StyleID="s63" ss:AutoFitWidth="0" ss:Width="210"/>
   <Column ss:StyleID="s63" ss:AutoFitWidth="0" ss:Width="67.5"/>
   <Column ss:StyleID="s63" ss:AutoFitWidth="0" ss:Width="106.5"/>
   <Column ss:StyleID="s63" ss:AutoFitWidth="0" ss:Width="132"/>
   <Row>
    <Cell ss:MergeAcross="7" ss:StyleID="s73"><Data ss:Type="String">${title}</Data></Cell>
   </Row>
   <Row>
    <Cell ss:StyleID="s74"><Data ss:Type="String">校区</Data></Cell>
    <Cell ss:StyleID="s74"><Data ss:Type="String">开课学院</Data></Cell>
    <Cell ss:StyleID="s74"><Data ss:Type="String">课程代码</Data></Cell>
    <Cell ss:StyleID="s74"><Data ss:Type="String">课程名称</Data></Cell>
    <Cell ss:StyleID="s74"><Data ss:Type="String">考试时间</Data></Cell>
    <Cell ss:StyleID="s74"><Data ss:Type="String">考试人数</Data></Cell>
    <Cell ss:StyleID="s74"><Data ss:Type="String">教室名称</Data></Cell>
    <Cell ss:StyleID="s74"><Data ss:Type="String">监考老师</Data></Cell>
   </Row>
<#list listSheet as item>
   <Row>
    <Cell ss:MergeDown="${item["rowNumber"]}" ss:StyleID="s75"><Data ss:Type="String">${item["campus"]}(${item["day"]}号)</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${item["dto"].faculty?replace(",","&#x000A;")}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${item["dto"].courseCode?replace(",","&#x000A;")}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${item["dto"].courseName?replace(",","&#x000A;")}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${item["dto"].examTime?default("")}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${item["dto"].roomNumber?default("")}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${item["dto"].roomName?default("")}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${item["dto"].teacherName?default("")}</Data></Cell>
   </Row>
 <#if item["list"]?size gt 0 >
  <#list item["list"] as myItem>
   <Row>
    <Cell ss:Index="2" ss:StyleID="s76"><Data ss:Type="String">${myItem.faculty}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${myItem.courseCode}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${myItem.courseName}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${myItem.examTime?default("")}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${myItem.roomNumber?default("")}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${myItem.roomName?default("")}</Data></Cell>
    <Cell ss:StyleID="s76"><Data ss:Type="String">${myItem.teacherName?default("")}</Data></Cell>
   </Row>
</#list>
  </#if>
</#list>
  </Table>
  <WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
   <PageSetup>
    <Header x:Margin="0.3"/>
    <Footer x:Margin="0.3"/>
    <PageMargins x:Bottom="0.75" x:Left="0.7" x:Right="0.7" x:Top="0.75"/>
   </PageSetup>
   <Print>
    <ValidPrinterInfo/>
    <PaperSizeIndex>9</PaperSizeIndex>
    <HorizontalResolution>300</HorizontalResolution>
    <VerticalResolution>300</VerticalResolution>
   </Print>
   <Selected/>
   <Panes>
    <Pane>
     <Number>3</Number>
     <ActiveRow>10</ActiveRow>
     <ActiveCol>7</ActiveCol>
    </Pane>
   </Panes>
   <ProtectObjects>False</ProtectObjects>
   <ProtectScenarios>False</ProtectScenarios>
  </WorksheetOptions>
 </Worksheet>
</Workbook>
