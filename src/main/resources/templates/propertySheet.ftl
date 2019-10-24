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
  <LastSaved>2019-10-12T05:08:42Z</LastSaved>
  <Version>16.00</Version>
 </DocumentProperties>
 <OfficeDocumentSettings xmlns="urn:schemas-microsoft-com:office:office">
  <AllowPNG/>
 </OfficeDocumentSettings>
 <ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel">
  <WindowHeight>12645</WindowHeight>
  <WindowWidth>22260</WindowWidth>
  <WindowTopX>32760</WindowTopX>
  <WindowTopY>32760</WindowTopY>
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
  <Style ss:ID="s62">
   <NumberFormat ss:Format="@"/>
  </Style>
  <Style ss:ID="s64">
   <Alignment ss:Horizontal="Center" ss:Vertical="Bottom"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <NumberFormat ss:Format="@"/>
  </Style>
  <Style ss:ID="s65">
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
  <Table ss:ExpandedColumnCount="5"  x:FullColumns="1"
   x:FullRows="1" ss:StyleID="s62" ss:DefaultColumnWidth="54"
   ss:DefaultRowHeight="14.25">
   <Column ss:StyleID="s62" ss:AutoFitWidth="0" ss:Width="146.25"/>
   <Column ss:StyleID="s62" ss:AutoFitWidth="0" ss:Width="160.5"/>
   <Column ss:StyleID="s62" ss:AutoFitWidth="0" ss:Width="219.75"/>
   <Column ss:StyleID="s62" ss:AutoFitWidth="0" ss:Width="254.25"/>
   <Column ss:StyleID="s62" ss:AutoFitWidth="0" ss:Width="111"/>
<#if  sheetMap??&& sheetMap?size gt 0 >
 <#list sheetMap ? keys as key>
   <Row>
    <Cell ss:MergeAcross="4" ss:StyleID="s64"><Data ss:Type="String">${title}(${key})</Data></Cell>
   </Row>
   <Row>
    <Cell ss:StyleID="s64"><Data ss:Type="String">开课学院</Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String">课程代码</Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String">课程名称</Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String">考试时间</Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String">教室名称</Data></Cell>
   </Row>
  <#list sheetMap[key] as item>
   <Row>
    <Cell ss:StyleID="s65"><Data ss:Type="String">${item.faculty}</Data></Cell>
    <Cell ss:StyleID="s65"><Data ss:Type="String">${item.courseCode}</Data></Cell>
    <Cell ss:StyleID="s65"><Data ss:Type="String">${item.courseName}</Data></Cell>
    <Cell ss:StyleID="s65"><Data ss:Type="String">${item.examTime?default("")}</Data></Cell>
    <Cell ss:StyleID="s65"><Data ss:Type="String">${item.roomName?default("")}</Data></Cell>
   </Row>
  </#list>
 </#list>
</#if>
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
    <HorizontalResolution>600</HorizontalResolution>
    <VerticalResolution>600</VerticalResolution>
   </Print>
   <Selected/>
   <Panes>
    <Pane>
     <Number>3</Number>
     <ActiveRow>12</ActiveRow>
     <ActiveCol>2</ActiveCol>
    </Pane>
   </Panes>
   <ProtectObjects>False</ProtectObjects>
   <ProtectScenarios>False</ProtectScenarios>
  </WorksheetOptions>
 </Worksheet>
</Workbook>
