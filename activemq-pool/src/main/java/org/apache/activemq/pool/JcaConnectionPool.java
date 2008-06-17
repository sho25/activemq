begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2006 the original author or authors.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|pool
package|;
end_package

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
name|transaction
operator|.
name|TransactionManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
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
name|ActiveMQConnection
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
name|ra
operator|.
name|LocalAndXATransaction
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
name|pool
operator|.
name|ObjectPoolFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geronimo
operator|.
name|transaction
operator|.
name|manager
operator|.
name|WrapperNamedXAResource
import|;
end_import

begin_class
specifier|public
class|class
name|JcaConnectionPool
extends|extends
name|XaConnectionPool
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|public
name|JcaConnectionPool
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|,
name|ObjectPoolFactory
name|poolFactory
parameter_list|,
name|TransactionManager
name|transactionManager
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|connection
argument_list|,
name|poolFactory
argument_list|,
name|transactionManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|protected
name|XAResource
name|createXaResource
parameter_list|(
name|PooledSession
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|XAResource
name|xares
init|=
operator|new
name|LocalAndXATransaction
argument_list|(
name|session
operator|.
name|getSession
argument_list|()
operator|.
name|getTransactionContext
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|xares
operator|=
operator|new
name|WrapperNamedXAResource
argument_list|(
name|xares
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|xares
return|;
block|}
block|}
end_class

end_unit

