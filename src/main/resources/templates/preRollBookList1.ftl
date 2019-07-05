<?xml version="1.0"?>
<?mso-application progid="Excel.Sheet"?>
<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:o="urn:schemas-microsoft-com:office:office"
 xmlns:x="urn:schemas-microsoft-com:office:excel"
 xmlns:dt="uuid:C2F41010-65B3-11d1-A29F-00AA00C14882"
 xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"
 xmlns:html="http://www.w3.org/TR/REC-html40">
 <DocumentProperties xmlns="urn:schemas-microsoft-com:office:office">
  <Author>dell</Author>
  <LastAuthor>lenovo</LastAuthor>
  <Created>2012-06-06T01:30:27Z</Created>
  <LastSaved>2019-05-08T10:48:35Z</LastSaved>
  <Version>16.00</Version>
 </DocumentProperties>
 <CustomDocumentProperties xmlns="urn:schemas-microsoft-com:office:office">
  <KSOProductBuildVer dt:dt="string">2052-9.1.0.4855</KSOProductBuildVer>
 </CustomDocumentProperties>
 <OfficeDocumentSettings xmlns="urn:schemas-microsoft-com:office:office">
  <AllowPNG/>
 </OfficeDocumentSettings>
 <ExcelWorkbook xmlns="urn:schemas-microsoft-com:office:excel">
  <WindowHeight>9840</WindowHeight>
  <WindowWidth>24000</WindowWidth>
  <WindowTopX>32760</WindowTopX>
  <WindowTopY>32760</WindowTopY>
  <ProtectStructure>False</ProtectStructure>
  <ProtectWindows>False</ProtectWindows>
 </ExcelWorkbook>
 <Styles>
  <Style ss:ID="Default" ss:Name="Normal">
   <Alignment ss:Vertical="Center"/>
   <Borders/>
   <Font ss:FontName="宋体" x:CharSet="134" ss:Size="12"/>
   <Interior/>
   <NumberFormat/>
   <Protection/>
  </Style>
  <Style ss:ID="s62">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <Font ss:FontName="宋体" x:CharSet="134" ss:Bold="1"/>
  </Style>
  <Style ss:ID="s63">
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
   <Font ss:FontName="宋体" x:CharSet="134"/>
  </Style>
  <Style ss:ID="s64">
   <Borders>
    <Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Left" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Right" ss:LineStyle="Continuous" ss:Weight="1"/>
    <Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="1"/>
   </Borders>
  </Style>
  <Style ss:ID="s71">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
   <Borders/>
   <Font ss:FontName="宋体" x:CharSet="134"/>
  </Style>
  <Style ss:ID="s72">
   <Alignment ss:Horizontal="Left" ss:Vertical="Center"/>
   <Borders/>
   <Font ss:FontName="宋体" x:CharSet="134"/>
  </Style>
  <Style ss:ID="s73">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
   <Font ss:FontName="宋体" x:CharSet="134"/>
  </Style>
  <Style ss:ID="s74">
   <Alignment ss:Horizontal="Left" ss:Vertical="Center"/>
   <Font ss:FontName="宋体" x:CharSet="134"/>
  </Style>
  <Style ss:ID="s75">
   <Alignment ss:Horizontal="Center" ss:Vertical="Center"/>
   <Borders/>
   <Font ss:FontName="宋体" x:CharSet="134" ss:Size="14" ss:Bold="1"/>
  </Style>
  <Style ss:ID="s76">
   <Alignment ss:Vertical="Center"/>
   <Font ss:FontName="宋体" x:CharSet="134"/>
  </Style>
 </Styles>
 <Worksheet ss:Name="Sheet1">
   <Table x:FullColumns="1"
   x:FullRows="1">
   <Column ss:AutoFitWidth="0" ss:Width="48.75"/>
   <Column ss:AutoFitWidth="0" ss:Width="58.5"/>
   <Column ss:AutoFitWidth="0" ss:Width="60"/>
   <Column ss:AutoFitWidth="0" ss:Width="48"/>
   <Column ss:AutoFitWidth="0" ss:Width="35.25"/>
   <Column ss:AutoFitWidth="0" ss:Width="33.75"/>
   <Column ss:AutoFitWidth="0" ss:Width="30.75"/>
   <Column ss:AutoFitWidth="0" ss:Width="31.5"/>
   <Column ss:AutoFitWidth="0" ss:Width="33"/>
   <Column ss:AutoFitWidth="0" ss:Width="31.5"/>
   <Column ss:AutoFitWidth="0" ss:Width="30.75"/>
   <Column ss:AutoFitWidth="0" ss:Width="30" ss:Span="1"/>
   <Column ss:Index="14" ss:AutoFitWidth="0" ss:Width="29.25" ss:Span="2"/>
   <Column ss:Index="17" ss:AutoFitWidth="0" ss:Width="30" ss:Span="1"/>
   <Column ss:Index="19" ss:AutoFitWidth="0" ss:Width="29.25" ss:Span="1"/>
   <Column ss:Index="21" ss:AutoFitWidth="0" ss:Width="28.5"/>
   <Row ss:AutoFitHeight="0" ss:Height="21.9375">
    <Cell ss:MergeAcross="20" ss:StyleID="s75"><Data ss:Type="String">${calendar}</Data></Cell>
   </Row>
   <#--<Row ss:AutoFitHeight="0" ss:Height="21">
    <Cell ss:MergeAcross="20" ss:StyleID="s75"><Data ss:Type="String">2018-2019学年第二学期</Data></Cell>
   </Row>-->
   <Row>
    <Cell ss:MergeAcross="4" ss:StyleID="s74"><Data ss:Type="String">课程序号：${item.classCode}</Data></Cell>
    <Cell ss:MergeAcross="15" ss:StyleID="s76"><Data ss:Type="String">课程名称：${item.courseName}</Data></Cell>
   </Row>
   <Row>
    <Cell ss:MergeAcross="4" ss:StyleID="s72"><Data ss:Type="String">教师名称：${item.teacherName}</Data></Cell>
    <Cell ss:MergeAcross="15" ss:StyleID="s72"><Data ss:Type="String">课程安排：${item.teachingTimeAndRoom} </Data></Cell>
   </Row>
   <Row>
    <Cell ss:MergeDown="1" ss:StyleID="s74"><Data ss:Type="String">说明： </Data></Cell>
    <Cell ss:MergeAcross="3" ss:StyleID="s71"><Data ss:Type="String">                            </Data></Cell>
    <Cell ss:MergeAcross="15" ss:StyleID="s72"><Data ss:Type="String">带*号的学生与其他课程冲突</Data></Cell>
   </Row>
   <Row>
    <Cell ss:Index="2" ss:MergeAcross="3" ss:StyleID="s73"><Data ss:Type="String">                           </Data></Cell>
    <Cell ss:MergeAcross="15" ss:StyleID="s74"><Data ss:Type="String">出勤 √   旷课 χ   病假 Ο   事假 Δ   迟到 Φ             任课老师签名：     年    月    日</Data></Cell>
   </Row>
   <Row>
    <Cell ss:StyleID="s62"><Data ss:Type="String">序号</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="String">学号</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="String">姓名</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="String">性别</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="String">年级</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="String">学院</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">1</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">2</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">3</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">4</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">5</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">6</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">7</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">8</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">9</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">10</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">11</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">12</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">13</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">14</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">15</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">16</Data></Cell>
    <Cell ss:StyleID="s62"><Data ss:Type="Number">17</Data></Cell>
   </Row>

   <#list list as item>
   <Row ss:AutoFitHeight="0">
    <Cell ss:StyleID="s63" ss:MergeDown="${lineNumber}"><Data ss:Type="Number">${item_index + 1}</Data></Cell>
    <Cell ss:StyleID="s63" ss:MergeDown="${lineNumber}"><Data ss:Type="String">${item.studentCode}</Data></Cell>
    <Cell ss:StyleID="s63" ss:MergeDown="${lineNumber}"><Data ss:Type="String">${item.exportName}</Data></Cell>
    <Cell ss:StyleID="s63" ss:MergeDown="${lineNumber}"><Data ss:Type="String">${item.sex}</Data></Cell>
    <Cell ss:StyleID="s63" ss:MergeDown="${lineNumber}"><Data ss:Type="Number">${item.grade}</Data></Cell>
    <Cell ss:StyleID="s63" ss:MergeDown="${lineNumber}"><Data ss:Type="String">${item.faculty}</Data></Cell>
       <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
       <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
   </Row>
    <#list lineList as line>
    <Row  ss:AutoFitHeight="0">
    <Cell ss:Index="7" ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s63"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    <Cell ss:StyleID="s64"><Data ss:Type="String"></Data></Cell>
    </Row>
    </#list>
   </#list>
   <Row ss:AutoFitHeight="0"/>
  </Table>
  <WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
   <PageSetup>
    <Header x:Margin="0.51111111111111107"/>
    <Footer x:Margin="0.51111111111111107"/>
   </PageSetup>
   <Print>
    <ValidPrinterInfo/>
    <PaperSizeIndex>9</PaperSizeIndex>
    <VerticalResolution>0</VerticalResolution>
   </Print>
   <PageBreakZoom>100</PageBreakZoom>
   <Selected/>
   <Panes>
    <Pane>
     <Number>3</Number>
     <ActiveRow>11</ActiveRow>
     <ActiveCol>8</ActiveCol>
    </Pane>
   </Panes>
   <ProtectObjects>False</ProtectObjects>
   <ProtectScenarios>False</ProtectScenarios>
  </WorksheetOptions>
 </Worksheet>
 <Worksheet ss:Name="Sheet2">
  <Table ss:ExpandedColumnCount="1" ss:ExpandedRowCount="1" x:FullColumns="1"
   x:FullRows="1" ss:DefaultColumnWidth="54" ss:DefaultRowHeight="14.25">
  </Table>
  <WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
   <PageSetup>
    <Header x:Margin="0.51111111111111107"/>
    <Footer x:Margin="0.51111111111111107"/>
   </PageSetup>
   <Print>
    <ValidPrinterInfo/>
    <PaperSizeIndex>9</PaperSizeIndex>
    <VerticalResolution>0</VerticalResolution>
   </Print>
   <PageBreakZoom>100</PageBreakZoom>
   <ProtectObjects>False</ProtectObjects>
   <ProtectScenarios>False</ProtectScenarios>
  </WorksheetOptions>
 </Worksheet>
 <Worksheet ss:Name="Sheet3">
  <Table ss:ExpandedColumnCount="1" ss:ExpandedRowCount="1" x:FullColumns="1"
   x:FullRows="1" ss:DefaultColumnWidth="54" ss:DefaultRowHeight="14.25">
  </Table>
  <WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
   <PageSetup>
    <Header x:Margin="0.51111111111111107"/>
    <Footer x:Margin="0.51111111111111107"/>
   </PageSetup>
   <Print>
    <ValidPrinterInfo/>
    <PaperSizeIndex>9</PaperSizeIndex>
    <VerticalResolution>0</VerticalResolution>
   </Print>
   <PageBreakZoom>100</PageBreakZoom>
   <ProtectObjects>False</ProtectObjects>
   <ProtectScenarios>False</ProtectScenarios>
  </WorksheetOptions>
 </Worksheet>
</Workbook>
