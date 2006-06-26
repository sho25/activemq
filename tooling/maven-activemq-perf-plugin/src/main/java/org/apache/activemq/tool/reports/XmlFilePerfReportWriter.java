begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|reports
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|reports
operator|.
name|plugins
operator|.
name|ReportPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|reports
operator|.
name|plugins
operator|.
name|ThroughputReportPlugin
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
specifier|public
class|class
name|XmlFilePerfReportWriter
extends|extends
name|AbstractPerfReportWriter
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|XmlFilePerfReportWriter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|File
name|tempLogFile
decl_stmt|;
specifier|private
name|PrintWriter
name|tempLogFileWriter
decl_stmt|;
specifier|private
name|File
name|xmlFile
decl_stmt|;
specifier|private
name|PrintWriter
name|xmlFileWriter
decl_stmt|;
specifier|private
name|String
name|reportDir
decl_stmt|;
specifier|private
name|String
name|reportName
decl_stmt|;
specifier|private
name|Map
name|testPropsMap
decl_stmt|;
specifier|private
name|List
name|testPropsList
decl_stmt|;
specifier|public
name|XmlFilePerfReportWriter
parameter_list|()
block|{
name|this
argument_list|(
literal|""
argument_list|,
literal|"PerformanceReport.xml"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XmlFilePerfReportWriter
parameter_list|(
name|String
name|reportDir
parameter_list|,
name|String
name|reportName
parameter_list|)
block|{
name|this
operator|.
name|testPropsMap
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|testPropsList
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|reportDir
operator|=
name|reportDir
expr_stmt|;
name|this
operator|.
name|reportName
operator|=
name|reportName
expr_stmt|;
block|}
specifier|public
name|void
name|openReportWriter
parameter_list|()
block|{
if|if
condition|(
name|tempLogFile
operator|==
literal|null
condition|)
block|{
name|tempLogFile
operator|=
name|createTempLogFile
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|// Disable auto-flush and allocate 100kb of buffer
name|tempLogFileWriter
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|tempLogFile
argument_list|)
argument_list|,
literal|102400
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|closeReportWriter
parameter_list|()
block|{
comment|// Flush and close log file writer
name|tempLogFileWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tempLogFileWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|writeToXml
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getReportDir
parameter_list|()
block|{
return|return
name|reportDir
return|;
block|}
specifier|public
name|void
name|setReportDir
parameter_list|(
name|String
name|reportDir
parameter_list|)
block|{
name|this
operator|.
name|reportDir
operator|=
name|reportDir
expr_stmt|;
block|}
specifier|public
name|String
name|getReportName
parameter_list|()
block|{
return|return
name|reportName
return|;
block|}
specifier|public
name|void
name|setReportName
parameter_list|(
name|String
name|reportName
parameter_list|)
block|{
name|this
operator|.
name|reportName
operator|=
name|reportName
expr_stmt|;
block|}
specifier|public
name|File
name|getXmlFile
parameter_list|()
block|{
return|return
name|xmlFile
return|;
block|}
specifier|public
name|void
name|setXmlFile
parameter_list|(
name|File
name|xmlFile
parameter_list|)
block|{
name|this
operator|.
name|xmlFile
operator|=
name|xmlFile
expr_stmt|;
block|}
specifier|public
name|void
name|writeInfo
parameter_list|(
name|String
name|info
parameter_list|)
block|{
name|tempLogFileWriter
operator|.
name|println
argument_list|(
literal|"[INFO]"
operator|+
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|writeCsvData
parameter_list|(
name|int
name|csvType
parameter_list|,
name|String
name|csvData
parameter_list|)
block|{
if|if
condition|(
name|csvType
operator|==
name|ReportPlugin
operator|.
name|REPORT_PLUGIN_THROUGHPUT
condition|)
block|{
name|tempLogFileWriter
operator|.
name|println
argument_list|(
literal|"[TP-DATA]"
operator|+
name|csvData
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|csvType
operator|==
name|ReportPlugin
operator|.
name|REPORT_PLUGIN_CPU
condition|)
block|{
name|tempLogFileWriter
operator|.
name|println
argument_list|(
literal|"[CPU-DATA]"
operator|+
name|csvData
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|writeProperties
parameter_list|(
name|String
name|header
parameter_list|,
name|Properties
name|props
parameter_list|)
block|{
name|testPropsMap
operator|.
name|put
argument_list|(
name|header
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|writeProperties
parameter_list|(
name|Properties
name|props
parameter_list|)
block|{
name|testPropsList
operator|.
name|add
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|File
name|createTempLogFile
parameter_list|()
block|{
name|File
name|f
decl_stmt|;
try|try
block|{
name|f
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"tmpPL"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|f
operator|=
operator|new
name|File
argument_list|(
literal|"tmpPL"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|".tmp"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
return|return
name|f
return|;
block|}
specifier|protected
name|File
name|createXmlFile
parameter_list|()
block|{
name|String
name|filename
init|=
operator|(
name|getReportName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
condition|?
name|getReportName
argument_list|()
else|:
operator|(
name|getReportName
argument_list|()
operator|+
literal|".xml"
operator|)
operator|)
decl_stmt|;
name|String
name|path
init|=
operator|(
name|getReportDir
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|getReportDir
argument_list|()
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|path
operator|+
name|filename
argument_list|)
return|;
block|}
specifier|protected
name|void
name|writeToXml
parameter_list|()
block|{
try|try
block|{
name|xmlFile
operator|=
name|createXmlFile
argument_list|()
expr_stmt|;
name|xmlFileWriter
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|xmlFile
argument_list|)
argument_list|)
expr_stmt|;
name|writeXmlHeader
argument_list|()
expr_stmt|;
name|writeXmlTestSettings
argument_list|()
expr_stmt|;
name|writeXmlLogFile
argument_list|()
expr_stmt|;
name|writeXmlPerfSummary
argument_list|()
expr_stmt|;
name|writeXmlFooter
argument_list|()
expr_stmt|;
name|xmlFileWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created performance report: "
operator|+
name|xmlFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|writeXmlHeader
parameter_list|()
block|{
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<testResult>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|writeXmlFooter
parameter_list|()
block|{
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"</testResult>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|writeXmlTestSettings
parameter_list|()
block|{
name|Properties
name|props
decl_stmt|;
comment|// Write test settings
for|for
control|(
name|Iterator
name|i
init|=
name|testPropsMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|props
operator|=
operator|(
name|Properties
operator|)
name|testPropsMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|writeMap
argument_list|(
name|key
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
name|int
name|count
init|=
literal|1
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|testPropsList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|props
operator|=
operator|(
name|Properties
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|writeMap
argument_list|(
literal|"settings"
operator|+
name|count
operator|++
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|writeXmlLogFile
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Write throughput data
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<property name='performanceData'>"
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<list>"
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tempLogFile
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"[TP-DATA]"
argument_list|)
condition|)
block|{
name|handleCsvData
argument_list|(
name|ReportPlugin
operator|.
name|REPORT_PLUGIN_THROUGHPUT
argument_list|,
name|line
operator|.
name|substring
argument_list|(
literal|"[TP-DATA]"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|parsePerfCsvData
argument_list|(
literal|"tpdata"
argument_list|,
name|line
operator|.
name|substring
argument_list|(
literal|"[TP-DATA]"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"[CPU-DATA]"
argument_list|)
condition|)
block|{
name|handleCsvData
argument_list|(
name|ReportPlugin
operator|.
name|REPORT_PLUGIN_CPU
argument_list|,
name|line
operator|.
name|substring
argument_list|(
literal|"[CPU-DATA]"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|parsePerfCsvData
argument_list|(
literal|"cpudata"
argument_list|,
name|line
operator|.
name|substring
argument_list|(
literal|"[CPU-DATA]"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"[INFO]"
argument_list|)
condition|)
block|{
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<info>"
operator|+
name|line
operator|+
literal|"</info>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<error>"
operator|+
name|line
operator|+
literal|"</error>"
argument_list|)
expr_stmt|;
block|}
block|}
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"</list>"
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"</property>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|writeXmlPerfSummary
parameter_list|()
block|{
comment|// Write throughput summary
name|Map
name|summary
init|=
name|getSummary
argument_list|(
name|ReportPlugin
operator|.
name|REPORT_PLUGIN_THROUGHPUT
argument_list|)
decl_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<property name='perfSummary'>"
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<props>"
argument_list|)
expr_stmt|;
name|String
name|val
decl_stmt|,
name|clientName
decl_stmt|,
name|clientVal
decl_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_TOTAL_TP
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"System Total Throughput: "
operator|+
name|val
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_TOTAL_TP
operator|+
literal|"'>"
operator|+
name|val
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_TOTAL_CLIENTS
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"System Total Clients: "
operator|+
name|val
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_TOTAL_CLIENTS
operator|+
literal|"'>"
operator|+
name|val
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_AVE_TP
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"System Average Throughput: "
operator|+
name|val
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_AVE_TP
operator|+
literal|"'>"
operator|+
name|val
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_AVE_EMM_TP
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"System Average Throughput Excluding Min/Max: "
operator|+
name|val
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_AVE_EMM_TP
operator|+
literal|"'>"
operator|+
name|val
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_AVE_CLIENT_TP
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"System Average Client Throughput: "
operator|+
name|val
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_AVE_CLIENT_TP
operator|+
literal|"'>"
operator|+
name|val
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_AVE_CLIENT_EMM_TP
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"System Average Client Throughput Excluding Min/Max: "
operator|+
name|val
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_SYS_AVE_CLIENT_EMM_TP
operator|+
literal|"'>"
operator|+
name|val
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_MIN_CLIENT_TP
argument_list|)
expr_stmt|;
name|clientName
operator|=
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|clientVal
operator|=
name|val
operator|.
name|substring
argument_list|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Min Client Throughput Per Sample: clientName="
operator|+
name|clientName
operator|+
literal|", value="
operator|+
name|clientVal
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_MIN_CLIENT_TP
operator|+
literal|"'>clientName="
operator|+
name|clientName
operator|+
literal|",value="
operator|+
name|clientVal
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_MAX_CLIENT_TP
argument_list|)
expr_stmt|;
name|clientName
operator|=
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|clientVal
operator|=
name|val
operator|.
name|substring
argument_list|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Max Client Throughput Per Sample: clientName="
operator|+
name|clientName
operator|+
literal|", value="
operator|+
name|clientVal
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_MAX_CLIENT_TP
operator|+
literal|"'>clientName="
operator|+
name|clientName
operator|+
literal|",value="
operator|+
name|clientVal
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_MIN_CLIENT_TOTAL_TP
argument_list|)
expr_stmt|;
name|clientName
operator|=
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|clientVal
operator|=
name|val
operator|.
name|substring
argument_list|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Min Client Total Throughput: clientName="
operator|+
name|clientName
operator|+
literal|", value="
operator|+
name|clientVal
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_MIN_CLIENT_TOTAL_TP
operator|+
literal|"'>clientName="
operator|+
name|clientName
operator|+
literal|",value="
operator|+
name|clientVal
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_MAX_CLIENT_TOTAL_TP
argument_list|)
expr_stmt|;
name|clientName
operator|=
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|clientVal
operator|=
name|val
operator|.
name|substring
argument_list|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Max Client Total Throughput: clientName="
operator|+
name|clientName
operator|+
literal|", value="
operator|+
name|clientVal
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_MAX_CLIENT_TOTAL_TP
operator|+
literal|"'>clientName="
operator|+
name|clientName
operator|+
literal|",value="
operator|+
name|clientVal
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_MIN_CLIENT_AVE_TP
argument_list|)
expr_stmt|;
name|clientName
operator|=
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|clientVal
operator|=
name|val
operator|.
name|substring
argument_list|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Min Average Client Throughput: clientName="
operator|+
name|clientName
operator|+
literal|", value="
operator|+
name|clientVal
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_MIN_CLIENT_AVE_TP
operator|+
literal|"'>clientName="
operator|+
name|clientName
operator|+
literal|",value="
operator|+
name|clientVal
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_MAX_CLIENT_AVE_TP
argument_list|)
expr_stmt|;
name|clientName
operator|=
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|clientVal
operator|=
name|val
operator|.
name|substring
argument_list|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Max Average Client Throughput: clientName="
operator|+
name|clientName
operator|+
literal|", value="
operator|+
name|clientVal
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_MAX_CLIENT_AVE_TP
operator|+
literal|"'>clientName="
operator|+
name|clientName
operator|+
literal|",value="
operator|+
name|clientVal
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_MIN_CLIENT_AVE_EMM_TP
argument_list|)
expr_stmt|;
name|clientName
operator|=
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|clientVal
operator|=
name|val
operator|.
name|substring
argument_list|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Min Average Client Throughput Excluding Min/Max: clientName="
operator|+
name|clientName
operator|+
literal|", value="
operator|+
name|clientVal
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_MIN_CLIENT_AVE_EMM_TP
operator|+
literal|"'>clientName="
operator|+
name|clientName
operator|+
literal|",value="
operator|+
name|clientVal
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|val
operator|=
operator|(
name|String
operator|)
name|summary
operator|.
name|get
argument_list|(
name|ThroughputReportPlugin
operator|.
name|KEY_MAX_CLIENT_AVE_EMM_TP
argument_list|)
expr_stmt|;
name|clientName
operator|=
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|clientVal
operator|=
name|val
operator|.
name|substring
argument_list|(
name|val
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Max Average Client Throughput Excluding Min/Max: clientName="
operator|+
name|clientName
operator|+
literal|", value="
operator|+
name|clientVal
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|ThroughputReportPlugin
operator|.
name|KEY_MAX_CLIENT_AVE_EMM_TP
operator|+
literal|"'>clientName="
operator|+
name|clientName
operator|+
literal|",value="
operator|+
name|clientVal
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"</props>"
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"</property>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|writeMap
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
name|map
parameter_list|)
block|{
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<property name='"
operator|+
name|name
operator|+
literal|"'>"
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<props>"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|propKey
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|propVal
init|=
name|map
operator|.
name|get
argument_list|(
name|propKey
argument_list|)
decl_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"<prop key='"
operator|+
name|propKey
operator|+
literal|"'>"
operator|+
name|propVal
operator|.
name|toString
argument_list|()
operator|+
literal|"</prop>"
argument_list|)
expr_stmt|;
block|}
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"</props>"
argument_list|)
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
literal|"</property>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|parsePerfCsvData
parameter_list|(
name|String
name|elementName
parameter_list|,
name|String
name|csvData
parameter_list|)
block|{
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|csvData
argument_list|,
literal|",;"
argument_list|)
decl_stmt|;
name|String
name|xmlElement
decl_stmt|;
name|xmlElement
operator|=
literal|"<"
operator|+
name|elementName
expr_stmt|;
name|String
name|data
decl_stmt|,
name|key
decl_stmt|,
name|val
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|data
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|key
operator|=
name|data
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
name|val
operator|=
name|data
operator|.
name|substring
argument_list|(
name|data
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|xmlElement
operator|+=
operator|(
literal|" "
operator|+
name|key
operator|+
literal|"='"
operator|+
name|val
operator|+
literal|"'"
operator|)
expr_stmt|;
block|}
name|xmlElement
operator|+=
literal|" />"
expr_stmt|;
name|xmlFileWriter
operator|.
name|println
argument_list|(
name|xmlElement
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

