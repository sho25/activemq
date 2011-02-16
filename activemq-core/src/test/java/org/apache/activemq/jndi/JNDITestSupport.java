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
name|jndi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Binding
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
name|javax
operator|.
name|naming
operator|.
name|NamingEnumeration
import|;
end_import

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
name|javax
operator|.
name|naming
operator|.
name|spi
operator|.
name|InitialContextFactory
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ActiveMQConnectionFactory
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JNDITestSupport
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
name|LOG
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JNDITestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Hashtable
name|environment
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
specifier|protected
name|Context
name|context
decl_stmt|;
specifier|protected
name|void
name|assertConnectionFactoryPresent
parameter_list|(
name|String
name|lookupName
parameter_list|)
throws|throws
name|NamingException
block|{
name|Object
name|connectionFactory
init|=
name|context
operator|.
name|lookup
argument_list|(
name|lookupName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a ConnectionFactory for key: "
operator|+
name|lookupName
operator|+
literal|" but got: "
operator|+
name|connectionFactory
argument_list|,
name|connectionFactory
operator|instanceof
name|ConnectionFactory
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertBinding
parameter_list|(
name|Binding
name|binding
parameter_list|)
throws|throws
name|NamingException
block|{
name|Object
name|object
init|=
name|binding
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have got a child context but got: "
operator|+
name|object
argument_list|,
name|object
operator|instanceof
name|Context
argument_list|)
expr_stmt|;
name|Context
name|childContext
init|=
operator|(
name|Context
operator|)
name|object
decl_stmt|;
name|NamingEnumeration
name|iter
init|=
name|childContext
operator|.
name|listBindings
argument_list|(
literal|""
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|Binding
name|destinationBinding
init|=
operator|(
name|Binding
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found destination: "
operator|+
name|destinationBinding
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|destination
init|=
name|destinationBinding
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have a Destination but got: "
operator|+
name|destination
argument_list|,
name|destination
operator|instanceof
name|Destination
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|configureEnvironment
argument_list|()
expr_stmt|;
name|InitialContextFactory
name|factory
init|=
operator|new
name|ActiveMQInitialContextFactory
argument_list|()
decl_stmt|;
name|context
operator|=
name|factory
operator|.
name|getInitialContext
argument_list|(
name|environment
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"No context created"
argument_list|,
name|context
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Stops all existing ActiveMQConnectionFactory in Context.      *      * @throws javax.naming.NamingException      */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|NamingException
throws|,
name|JMSException
block|{
name|NamingEnumeration
name|iter
init|=
name|context
operator|.
name|listBindings
argument_list|(
literal|""
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|Binding
name|binding
init|=
operator|(
name|Binding
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|connFactory
init|=
name|binding
operator|.
name|getObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|connFactory
operator|instanceof
name|ActiveMQConnectionFactory
condition|)
block|{
comment|// ((ActiveMQConnectionFactory) connFactory).stop();
block|}
block|}
block|}
specifier|protected
name|void
name|configureEnvironment
parameter_list|()
block|{
name|environment
operator|.
name|put
argument_list|(
literal|"brokerURL"
argument_list|,
literal|"vm://localhost"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertDestinationExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|NamingException
block|{
name|Object
name|object
init|=
name|context
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a Destination for name: "
operator|+
name|name
operator|+
literal|" but instead found: "
operator|+
name|object
argument_list|,
name|object
operator|instanceof
name|Destination
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

