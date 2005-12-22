begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|config
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|test
operator|.
name|JmsTopicSendReceiveWithTwoConnectionsTest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|BrokerXmlConfigTest
extends|extends
name|JmsTopicSendReceiveWithTwoConnectionsTest
block|{
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
comment|// START SNIPPET: bean
comment|// configure the connection factory using
comment|// normal Java Bean property methods
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
comment|// configure the embedded broker using an XML config file
comment|// which is either a URL or a resource on the classpath
comment|// TODO ...
comment|//connectionFactory.setBrokerXmlConfig("file:src/sample-conf/default.xml");
comment|// you only need to configure the broker URL if you wish to change the
comment|// default connection mechanism, which in this test case we do
name|connectionFactory
operator|.
name|setBrokerURL
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
comment|// END SNIPPET: bean
return|return
name|connectionFactory
return|;
block|}
block|}
end_class

end_unit

