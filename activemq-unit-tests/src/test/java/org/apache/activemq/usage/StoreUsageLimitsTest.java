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
name|usage
package|;
end_package

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
name|File
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|EmbeddedBrokerTestSupport
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
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_class
specifier|public
class|class
name|StoreUsageLimitsTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|final
name|int
name|WAIT_TIME_MILLS
init|=
literal|20
operator|*
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|limitsLogLevel
init|=
literal|"error"
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|setCheckLimitsLogLevel
argument_list|(
name|limitsLogLevel
argument_list|)
expr_stmt|;
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|testCheckLimitsLogLevel
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"target/activemq-test.log"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"target/activemq-test.log was not created."
argument_list|)
expr_stmt|;
block|}
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
name|boolean
name|foundUsage
init|=
literal|false
decl_stmt|;
try|try
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
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
name|contains
argument_list|(
operator|new
name|String
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|Long
operator|.
name|MAX_VALUE
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|)
argument_list|)
argument_list|)
operator|&&
name|line
operator|.
name|contains
argument_list|(
name|limitsLogLevel
operator|.
name|toUpperCase
argument_list|()
argument_list|)
condition|)
block|{
name|foundUsage
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|foundUsage
condition|)
name|fail
argument_list|(
literal|"checkLimitsLogLevel message did not write to log target/activemq-test.log"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
