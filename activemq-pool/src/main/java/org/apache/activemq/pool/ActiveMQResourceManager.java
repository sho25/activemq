begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Session
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
name|transaction
operator|.
name|TransactionManager
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
name|ActiveMQSession
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
name|util
operator|.
name|IOExceptionSupport
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
name|RecoverableTransactionManager
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
name|NamedXAResource
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

begin_comment
comment|/**  * This class allows wiring the ActiveMQ broker and the Geronimo transaction manager  * in a way that will allow the transaction manager to correctly recover XA transactions.  *  * For example, it can be used the following way:  *<pre>  *<bean id="activemqConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">  *<property name="brokerURL" value="tcp://localhost:61616" />  *</bean>  *  *<bean id="pooledConnectionFactory" class="org.apache.activemq.pool.PooledConnectionFactoryFactoryBean">  *<property name="maxConnections" value="8" />  *<property name="transactionManager" ref="transactionManager" />  *<property name="connectionFactory" ref="activemqConnectionFactory" />  *<property name="resourceName" value="activemq.broker" />  *</bean>  *  *<bean id="resourceManager" class="org.apache.activemq.pool.ActiveMQResourceManager" init-method="recoverResource">  *<property name="transactionManager" ref="transactionManager" />  *<property name="connectionFactory" ref="activemqConnectionFactory" />  *<property name="resourceName" value="activemq.broker" />  *</bean>  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQResourceManager
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOGGER
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ActiveMQResourceManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|resourceName
decl_stmt|;
specifier|private
name|TransactionManager
name|transactionManager
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|public
name|void
name|recoverResource
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
operator|!
name|Recovery
operator|.
name|recover
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Resource manager is unrecoverable"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Resource manager is unrecoverable due to missing classes: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Error while recovering resource manager"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getResourceName
parameter_list|()
block|{
return|return
name|resourceName
return|;
block|}
specifier|public
name|void
name|setResourceName
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
name|this
operator|.
name|resourceName
operator|=
name|resourceName
expr_stmt|;
block|}
specifier|public
name|TransactionManager
name|getTransactionManager
parameter_list|()
block|{
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
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
name|connectionFactory
return|;
block|}
specifier|public
name|void
name|setConnectionFactory
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
comment|/**      * This class will ensure the broker is properly recovered when wired with      * the Geronimo transaction manager.      */
specifier|public
specifier|static
class|class
name|Recovery
block|{
specifier|public
specifier|static
name|boolean
name|isRecoverable
parameter_list|(
name|ActiveMQResourceManager
name|rm
parameter_list|)
block|{
return|return
name|rm
operator|.
name|getConnectionFactory
argument_list|()
operator|instanceof
name|ActiveMQConnectionFactory
operator|&&
name|rm
operator|.
name|getTransactionManager
argument_list|()
operator|instanceof
name|RecoverableTransactionManager
operator|&&
name|rm
operator|.
name|getResourceName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|rm
operator|.
name|getResourceName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|recover
parameter_list|(
name|ActiveMQResourceManager
name|rm
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isRecoverable
argument_list|(
name|rm
argument_list|)
condition|)
block|{
try|try
block|{
name|ActiveMQConnectionFactory
name|connFactory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|rm
operator|.
name|getConnectionFactory
argument_list|()
decl_stmt|;
name|ActiveMQConnection
name|activeConn
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|activeConn
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|NamedXAResource
name|namedXaResource
init|=
operator|new
name|WrapperNamedXAResource
argument_list|(
name|session
operator|.
name|getTransactionContext
argument_list|()
argument_list|,
name|rm
operator|.
name|getResourceName
argument_list|()
argument_list|)
decl_stmt|;
name|RecoverableTransactionManager
name|rtxManager
init|=
operator|(
name|RecoverableTransactionManager
operator|)
name|rm
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|rtxManager
operator|.
name|recoverResourceManager
argument_list|(
name|namedXaResource
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

