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
name|jndi
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_comment
comment|/**  * Test case for AMQ-141  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|CustomConnectionFactoryNameTest
extends|extends
name|ActiveMQInitialContextFactoryTest
block|{
specifier|public
name|void
name|testConnectionFactoriesArePresent
parameter_list|()
throws|throws
name|NamingException
block|{
name|super
operator|.
name|testConnectionFactoriesArePresent
argument_list|()
expr_stmt|;
name|assertConnectionFactoryPresent
argument_list|(
literal|"jms/Connection"
argument_list|)
expr_stmt|;
name|assertConnectionFactoryPresent
argument_list|(
literal|"jms/DURABLE_SUB_CONNECTION_FACTORY"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionFactoriesAreConfigured
parameter_list|()
throws|throws
name|NamingException
block|{
name|super
operator|.
name|testConnectionFactoriesArePresent
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory1
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|"jms/Connection"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|factory1
operator|.
name|getClientID
argument_list|()
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory2
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|"jms/DURABLE_SUB_CONNECTION_FACTORY"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"testclient"
argument_list|,
name|factory2
operator|.
name|getClientID
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getConnectionFactoryLookupName
parameter_list|()
block|{
return|return
literal|"myConnectionFactory"
return|;
block|}
specifier|protected
name|void
name|configureEnvironment
parameter_list|()
block|{
name|super
operator|.
name|configureEnvironment
argument_list|()
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
literal|"connectionFactoryNames"
argument_list|,
literal|" myConnectionFactory, jms/Connection, jms/DURABLE_SUB_CONNECTION_FACTORY"
argument_list|)
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
literal|"connection.jms/DURABLE_SUB_CONNECTION_FACTORY.clientID"
argument_list|,
literal|"testclient"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

