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
name|jms
operator|.
name|pool
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
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
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAConnectionFactory
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
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Name
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
name|spi
operator|.
name|ObjectFactory
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

begin_comment
comment|/**  * A pooled connection factory that automatically enlists  * sessions in the current active XA transaction if any.  */
end_comment

begin_class
specifier|public
class|class
name|XaPooledConnectionFactory
extends|extends
name|PooledConnectionFactory
implements|implements
name|ObjectFactory
implements|,
name|Serializable
implements|,
name|QueueConnectionFactory
implements|,
name|TopicConnectionFactory
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XaPooledConnectionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TransactionManager
name|transactionManager
decl_stmt|;
specifier|private
name|boolean
name|tmFromJndi
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|tmJndiName
init|=
literal|"java:/TransactionManager"
decl_stmt|;
specifier|public
name|TransactionManager
name|getTransactionManager
parameter_list|()
block|{
if|if
condition|(
name|transactionManager
operator|==
literal|null
operator|&&
name|tmFromJndi
condition|)
block|{
try|try
block|{
name|transactionManager
operator|=
operator|(
name|TransactionManager
operator|)
operator|new
name|InitialContext
argument_list|()
operator|.
name|lookup
argument_list|(
name|getTmJndiName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignored
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"exception on tmFromJndi: "
operator|+
name|getTmJndiName
argument_list|()
argument_list|,
name|ignored
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|transactionManager
return|;
block|}
specifier|public
name|void
name|setTransactionManager
parameter_list|(
name|TransactionManager
name|transactionManager
parameter_list|)
block|{
name|this
operator|.
name|transactionManager
operator|=
name|transactionManager
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConnectionPool
name|createConnectionPool
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
return|return
operator|new
name|XaConnectionPool
argument_list|(
name|connection
argument_list|,
name|getTransactionManager
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getObjectInstance
parameter_list|(
name|Object
name|obj
parameter_list|,
name|Name
name|name
parameter_list|,
name|Context
name|nameCtx
parameter_list|,
name|Hashtable
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|environment
parameter_list|)
throws|throws
name|Exception
block|{
name|setTmFromJndi
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|configFromJndiConf
argument_list|(
name|obj
argument_list|)
expr_stmt|;
if|if
condition|(
name|environment
operator|!=
literal|null
condition|)
block|{
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|this
argument_list|,
name|environment
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|private
name|void
name|configFromJndiConf
parameter_list|(
name|Object
name|rootContextName
parameter_list|)
block|{
if|if
condition|(
name|rootContextName
operator|instanceof
name|String
condition|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|rootContextName
decl_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
operator|+
literal|"/conf"
operator|+
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|InitialContext
name|ctx
init|=
operator|new
name|InitialContext
argument_list|()
decl_stmt|;
name|NamingEnumeration
name|bindings
init|=
name|ctx
operator|.
name|listBindings
argument_list|(
name|name
argument_list|)
decl_stmt|;
while|while
condition|(
name|bindings
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|Binding
name|bd
init|=
operator|(
name|Binding
operator|)
name|bindings
operator|.
name|next
argument_list|()
decl_stmt|;
name|IntrospectionSupport
operator|.
name|setProperty
argument_list|(
name|this
argument_list|,
name|bd
operator|.
name|getName
argument_list|()
argument_list|,
name|bd
operator|.
name|getObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"exception on config from jndi: "
operator|+
name|name
argument_list|,
name|ignored
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|String
name|getTmJndiName
parameter_list|()
block|{
return|return
name|tmJndiName
return|;
block|}
specifier|public
name|void
name|setTmJndiName
parameter_list|(
name|String
name|tmJndiName
parameter_list|)
block|{
name|this
operator|.
name|tmJndiName
operator|=
name|tmJndiName
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTmFromJndi
parameter_list|()
block|{
return|return
name|tmFromJndi
return|;
block|}
comment|/**      * Allow transaction manager resolution from JNDI (ee deployment)      * @param tmFromJndi      */
specifier|public
name|void
name|setTmFromJndi
parameter_list|(
name|boolean
name|tmFromJndi
parameter_list|)
block|{
name|this
operator|.
name|tmFromJndi
operator|=
name|tmFromJndi
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|QueueConnection
operator|)
name|createConnection
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|(
name|QueueConnection
operator|)
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|TopicConnection
operator|)
name|createConnection
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|(
name|TopicConnection
operator|)
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
block|}
end_class

end_unit
