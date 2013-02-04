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
name|karaf
operator|.
name|itest
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|httpclient
operator|.
name|HttpClient
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
name|httpclient
operator|.
name|UsernamePasswordCredentials
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
name|httpclient
operator|.
name|auth
operator|.
name|AuthScope
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
name|httpclient
operator|.
name|methods
operator|.
name|GetMethod
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
name|httpclient
operator|.
name|methods
operator|.
name|PostMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|junit
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|junit
operator|.
name|JUnit4TestRunner
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|JUnit4TestRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ActiveMQBrokerNdWebConsoleFeatureTest
extends|extends
name|ActiveMQBrokerFeatureTest
block|{
specifier|static
specifier|final
name|String
name|WEB_CONSOLE_URL
init|=
literal|"http://localhost:8181/activemqweb/"
decl_stmt|;
annotation|@
name|Configuration
specifier|public
specifier|static
name|Option
index|[]
name|configure
parameter_list|()
block|{
return|return
name|append
argument_list|(
name|CoreOptions
operator|.
name|mavenBundle
argument_list|(
literal|"commons-codec"
argument_list|,
literal|"commons-codec"
argument_list|)
operator|.
name|versionAsInProject
argument_list|()
argument_list|,
name|append
argument_list|(
name|CoreOptions
operator|.
name|mavenBundle
argument_list|(
literal|"commons-httpclient"
argument_list|,
literal|"commons-httpclient"
argument_list|)
operator|.
name|versionAsInProject
argument_list|()
argument_list|,
name|configureBrokerStart
argument_list|(
name|configure
argument_list|(
literal|"activemq-broker"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|produceMessage
parameter_list|(
name|String
name|nameAndPayload
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpClient
name|client
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"attempting publish via web console.."
argument_list|)
expr_stmt|;
comment|// set credentials
name|client
operator|.
name|getState
argument_list|()
operator|.
name|setCredentials
argument_list|(
operator|new
name|AuthScope
argument_list|(
name|AuthScope
operator|.
name|ANY_HOST
argument_list|,
name|AuthScope
operator|.
name|ANY_PORT
argument_list|)
argument_list|,
operator|new
name|UsernamePasswordCredentials
argument_list|(
name|USER
argument_list|,
name|PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
comment|// need to first get the secret
name|GetMethod
name|get
init|=
operator|new
name|GetMethod
argument_list|(
name|WEB_CONSOLE_URL
operator|+
literal|"send.jsp"
argument_list|)
decl_stmt|;
name|get
operator|.
name|setDoAuthentication
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Give console some time to start
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|i
operator|=
name|client
operator|.
name|executeMethod
argument_list|(
name|get
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|net
operator|.
name|ConnectException
name|ignored
parameter_list|)
block|{}
block|}
name|assertEquals
argument_list|(
literal|"get succeeded on "
operator|+
name|get
argument_list|,
literal|200
argument_list|,
name|get
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|response
init|=
name|get
operator|.
name|getResponseBodyAsString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|secretMarker
init|=
literal|"<input type=\"hidden\" name=\"secret\" value=\""
decl_stmt|;
name|String
name|secret
init|=
name|response
operator|.
name|substring
argument_list|(
name|response
operator|.
name|indexOf
argument_list|(
name|secretMarker
argument_list|)
operator|+
name|secretMarker
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|secret
operator|=
name|secret
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|secret
operator|.
name|indexOf
argument_list|(
literal|"\"/>"
argument_list|)
argument_list|)
expr_stmt|;
name|PostMethod
name|post
init|=
operator|new
name|PostMethod
argument_list|(
name|WEB_CONSOLE_URL
operator|+
literal|"sendMessage.action"
argument_list|)
decl_stmt|;
name|post
operator|.
name|setDoAuthentication
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|post
operator|.
name|addParameter
argument_list|(
literal|"secret"
argument_list|,
name|secret
argument_list|)
expr_stmt|;
name|post
operator|.
name|addParameter
argument_list|(
literal|"JMSText"
argument_list|,
name|nameAndPayload
argument_list|)
expr_stmt|;
name|post
operator|.
name|addParameter
argument_list|(
literal|"JMSDestination"
argument_list|,
name|nameAndPayload
argument_list|)
expr_stmt|;
name|post
operator|.
name|addParameter
argument_list|(
literal|"JMSDestinationType"
argument_list|,
literal|"queue"
argument_list|)
expr_stmt|;
comment|// execute the send
name|assertEquals
argument_list|(
literal|"post succeeded, "
operator|+
name|post
argument_list|,
literal|302
argument_list|,
name|client
operator|.
name|executeMethod
argument_list|(
name|post
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

