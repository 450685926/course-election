<?xml version="1.0"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:html="http://www.w3.org/TR/REC-html40">
 <DocumentProperties xmlns="urn:schemas-microsoft-com:office:office">
  <Author>Xiong,Zhengqing</Author>
  <LastAuthor>Xiong,Zhengqing</LastAuthor>
  <Created>2019-04-19T10:45:45Z</Created>
  <LastSaved>2019-04-19T10:45:26Z</LastSaved>
  <Version>15.00</Version>
 </DocumentProperties>
 <OfficeDocumentSettings xmlns="urn:schemas-microsoft-com:office:office">
  <AllowPNG/>
 </OfficeDocumentSettings>
 <ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel">
  <WindowHeight>9150</WindowHeight>
  <WindowWidth>14940</WindowWidth>
  <WindowTopX>360</WindowTopX>
  <WindowTopY>270</WindowTopY>
  <ProtectStructure>False</ProtectStructure>
  <ProtectWindows>False</ProtectWindows>
 </ExcelWorkbook>
 <Styles>
  <Style ss:ID="Default" ss:Name="Normal">
   <Alignment ss:Vertical="Bottom"/>
   <Borders/>
   <Font ss:FontName="Arial" x:Family="Swiss"/>
   <Interior/>
   <NumberFormat/>
   <Protection/>
  </Style>
  <Style ss:ID="m416668380">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <Font ss:FontName="宋体" x:CharSet="134"/>
   <NumberFormat ss:Format="@"/>
   <Protection ss:Protected="0"/>
  </Style>
  <Style ss:ID="s87">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <Font ss:FontName="宋体" x:CharSet="134" ss:Size="15" ss:Color="#000000"/>
   <Interior ss:Color="#CCCCFF" ss:Pattern="Solid"/>
   <Protection ss:Protected="0"/>
  </Style>
  <Style ss:ID="s89">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <Font ss:FontName="宋体" x:CharSet="134" ss:Size="15" ss:Color="#000000"/>
   <Interior ss:Color="#CCCCFF" ss:Pattern="Solid"/>
   <Protection ss:Protected="0"/>
  </Style>
  <Style ss:ID="s90">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center" ss:WrapText="1"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <NumberFormat ss:Format="@"/>
   <Protection ss:Protected="0"/>
  </Style>
 </Styles>
 <Worksheet ss:Name="sheet1">
  <Table ss:ExpandedColumnCount="999" ss:ExpandedRowCount="99" x:FullColumns="1"
   x:FullRows="1">
   <Column ss:AutoFitWidth="0" ss:Width="157.5" ss:Span="4"/>

   <Row ss:AutoFitHeight="0" ss:Height="27">
    <Cell ss:MergeAcross="4" ss:StyleID="s87"><ss:Data ss:Type="String"
      xmlns="http://www.w3.org/TR/REC-html40"><Font html:Color="#000000">${calendar}</Font></ss:Data></Cell>
   </Row>
   <Row ss:AutoFitHeight="0" ss:Height="18.9375">
    <Cell ss:StyleID="s89"><Data ss:Type="String">序号</Data></Cell>
    <Cell ss:StyleID="s89"><Data ss:Type="String">学号</Data></Cell>
    <Cell ss:StyleID="s89"><Data ss:Type="String">姓名</Data></Cell>
    <Cell ss:StyleID="s89"><Data ss:Type="String">性别</Data></Cell>
    <Cell ss:StyleID="s89"><Data ss:Type="String">年级</Data></Cell>
    <Cell ss:StyleID="s89"><Data ss:Type="String">学院</Data></Cell>
   </Row>
   <#list list as item>
   <Row ss:AutoFitHeight="0" ss:Height="14.0625">
    <Cell ss:StyleID="s90"><Data ss:Type="String">${item_index + 1}</Data></Cell>
    <Cell ss:StyleID="s90"><Data ss:Type="String">${item.studentCode}</Data></Cell>
    <Cell ss:StyleID="s90"><Data ss:Type="String">${item.name}</Data></Cell>
    <Cell ss:StyleID="s90"><Data ss:Type="String">${item.sex}</Data></Cell>
    <Cell ss:StyleID="s90"><Data ss:Type="String">${item.grade}</Data></Cell>
    <Cell ss:StyleID="s90"><Data ss:Type="String">${item.faculty}</Data></Cell>
   </Row>
   </#list>
  </Table>
  <WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
   <Print>
    <ValidPrinterInfo/>
    <HorizontalResolution>300</HorizontalResolution>
    <VerticalResolution>300</VerticalResolution>
   </Print>
   <Selected/>
   <Panes>
    <Pane>
     <Number>3</Number>
     <ActiveRow>9</ActiveRow>
     <ActiveCol>1</ActiveCol>
    </Pane>
   </Panes>
   <ProtectObjects>False</ProtectObjects>
   <ProtectScenarios>False</ProtectScenarios>
  </WorksheetOptions>
 </Worksheet>
</Workbook>
