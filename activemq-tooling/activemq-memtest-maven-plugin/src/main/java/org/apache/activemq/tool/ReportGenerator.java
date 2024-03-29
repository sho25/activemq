begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

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
name|FileOutputStream
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
name|util
operator|.
name|Enumeration
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|ReportGenerator
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ReportGenerator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|reportDirectory
decl_stmt|;
specifier|private
name|String
name|reportName
decl_stmt|;
specifier|private
name|PrintWriter
name|writer
decl_stmt|;
specifier|private
name|File
name|reportFile
decl_stmt|;
specifier|private
name|Properties
name|testSettings
decl_stmt|;
specifier|public
name|ReportGenerator
parameter_list|()
block|{     }
specifier|public
name|ReportGenerator
parameter_list|(
name|String
name|reportDirectory
parameter_list|,
name|String
name|reportName
parameter_list|)
block|{
name|this
operator|.
name|setReportDirectory
argument_list|(
name|reportDirectory
argument_list|)
expr_stmt|;
name|this
operator|.
name|setReportName
argument_list|(
name|reportName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startGenerateReport
parameter_list|()
block|{
name|File
name|reportDir
init|=
operator|new
name|File
argument_list|(
name|getReportDirectory
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create output directory if it doesn't exist.
if|if
condition|(
operator|!
name|reportDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|reportDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reportDir
operator|!=
literal|null
condition|)
block|{
name|reportFile
operator|=
operator|new
name|File
argument_list|(
name|this
operator|.
name|getReportDirectory
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|this
operator|.
name|getReportName
argument_list|()
operator|+
literal|".xml"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|this
operator|.
name|writer
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|reportFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// To change body of catch statement use
comment|// File | Settings | File Templates.
block|}
block|}
specifier|public
name|void
name|stopGenerateReport
parameter_list|()
block|{
name|writeWithIndent
argument_list|(
literal|0
argument_list|,
literal|"</test-report>"
argument_list|)
expr_stmt|;
name|this
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|this
operator|.
name|getWriter
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|" TEST REPORT OUTPUT : "
operator|+
name|reportFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addTestInformation
parameter_list|()
block|{
name|writeWithIndent
argument_list|(
literal|0
argument_list|,
literal|"<test-report>"
argument_list|)
expr_stmt|;
name|writeWithIndent
argument_list|(
literal|2
argument_list|,
literal|"<test-information>"
argument_list|)
expr_stmt|;
name|writeWithIndent
argument_list|(
literal|4
argument_list|,
literal|"<os-name>"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|+
literal|"</os-name>"
argument_list|)
expr_stmt|;
name|writeWithIndent
argument_list|(
literal|4
argument_list|,
literal|"<java-version>"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|+
literal|"</java-version>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addClientSettings
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|getTestSettings
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Enumeration
name|keys
init|=
name|getTestSettings
argument_list|()
operator|.
name|propertyNames
argument_list|()
decl_stmt|;
name|writeWithIndent
argument_list|(
literal|4
argument_list|,
literal|"<test-settings>"
argument_list|)
expr_stmt|;
name|String
name|key
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|key
operator|=
operator|(
name|String
operator|)
name|keys
operator|.
name|nextElement
argument_list|()
expr_stmt|;
name|writeWithIndent
argument_list|(
literal|6
argument_list|,
literal|"<"
operator|+
name|key
operator|+
literal|">"
operator|+
name|getTestSettings
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|+
literal|"</"
operator|+
name|key
operator|+
literal|">"
argument_list|)
expr_stmt|;
block|}
name|writeWithIndent
argument_list|(
literal|4
argument_list|,
literal|"</test-settings>"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|endTestInformation
parameter_list|()
block|{
name|writeWithIndent
argument_list|(
literal|2
argument_list|,
literal|"</test-information>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|startTestResult
parameter_list|(
name|long
name|checkpointInterval
parameter_list|)
block|{
name|long
name|intervalInSec
init|=
name|checkpointInterval
operator|/
literal|1000
decl_stmt|;
name|writeWithIndent
argument_list|(
literal|2
argument_list|,
literal|"<test-result checkpoint_interval_in_sec='"
operator|+
name|intervalInSec
operator|+
literal|"'>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|endTestResult
parameter_list|()
block|{
name|writeWithIndent
argument_list|(
literal|2
argument_list|,
literal|"</test-result>"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|writeWithIndent
parameter_list|(
name|int
name|indent
parameter_list|,
name|String
name|result
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indent
condition|;
operator|++
name|i
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PrintWriter
name|getWriter
parameter_list|()
block|{
return|return
name|this
operator|.
name|writer
return|;
block|}
specifier|public
name|String
name|getReportDirectory
parameter_list|()
block|{
return|return
name|reportDirectory
return|;
block|}
specifier|public
name|void
name|setReportDirectory
parameter_list|(
name|String
name|reportDirectory
parameter_list|)
block|{
name|this
operator|.
name|reportDirectory
operator|=
name|reportDirectory
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
name|Properties
name|getTestSettings
parameter_list|()
block|{
return|return
name|testSettings
return|;
block|}
specifier|public
name|void
name|setTestSettings
parameter_list|(
name|Properties
name|testSettings
parameter_list|)
block|{
name|this
operator|.
name|testSettings
operator|=
name|testSettings
expr_stmt|;
block|}
block|}
end_class

end_unit

