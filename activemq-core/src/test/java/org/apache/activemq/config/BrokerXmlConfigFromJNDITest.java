begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
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
name|apache
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
name|apache
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
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
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
name|util
operator|.
name|Hashtable
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|BrokerXmlConfigFromJNDITest
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
comment|// START SNIPPET: example
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
comment|// we could put these properties into a jndi.properties
comment|// on the classpath instead
name|Hashtable
name|properties
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"java.naming.factory.initial"
argument_list|,
literal|"org.apache.activemq.jndi.ActiveMQInitialContextFactory"
argument_list|)
expr_stmt|;
comment|// configure the embedded broker using an XML config file
comment|// which is either a URL or a resource on the classpath
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"basedir"
argument_list|,
literal|"."
argument_list|)
argument_list|)
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
literal|"vm://localhost?brokerConfig=xbean:file:"
operator|+
name|f
operator|+
literal|"/src/test/resources/activemq.xml"
argument_list|)
expr_stmt|;
name|InitialContext
name|context
init|=
operator|new
name|InitialContext
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|"ConnectionFactory"
argument_list|)
decl_stmt|;
comment|// END SNIPPET: example
return|return
name|connectionFactory
return|;
block|}
block|}
end_class

end_unit

