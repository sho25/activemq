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
name|ra
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
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|LocalTransaction
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
name|XAException
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
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|Xid
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
name|TransactionContext
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
comment|/**  * Used to provide a LocalTransaction and XAResource to a JMS session.  */
end_comment

begin_class
specifier|public
class|class
name|LocalAndXATransaction
implements|implements
name|XAResource
implements|,
name|LocalTransaction
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
name|LocalAndXATransaction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|TransactionContext
name|transactionContext
decl_stmt|;
specifier|private
name|boolean
name|inManagedTx
decl_stmt|;
specifier|public
name|LocalAndXATransaction
parameter_list|(
name|TransactionContext
name|transactionContext
parameter_list|)
block|{
name|this
operator|.
name|transactionContext
operator|=
name|transactionContext
expr_stmt|;
block|}
specifier|public
name|void
name|setInManagedTx
parameter_list|(
name|boolean
name|inManagedTx
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|inManagedTx
operator|=
name|inManagedTx
expr_stmt|;
if|if
condition|(
operator|!
name|inManagedTx
condition|)
block|{
name|transactionContext
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|begin
parameter_list|()
throws|throws
name|ResourceException
block|{
try|try
block|{
name|transactionContext
operator|.
name|begin
argument_list|()
expr_stmt|;
name|setInManagedTx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"begin failed."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|ResourceException
block|{
try|try
block|{
name|transactionContext
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"commit failed."
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|setInManagedTx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"commit failed."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|ResourceException
block|{
try|try
block|{
name|transactionContext
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"rollback failed."
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|setInManagedTx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"rollback failed."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|commit
parameter_list|(
name|Xid
name|arg0
parameter_list|,
name|boolean
name|arg1
parameter_list|)
throws|throws
name|XAException
block|{
name|transactionContext
operator|.
name|commit
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|end
parameter_list|(
name|Xid
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
throws|throws
name|XAException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} end {} with {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|this
block|,
name|arg0
block|,
name|arg1
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|transactionContext
operator|.
name|end
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|setInManagedTx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|XAException
operator|)
operator|new
name|XAException
argument_list|(
name|XAException
operator|.
name|XAER_PROTO
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|forget
parameter_list|(
name|Xid
name|arg0
parameter_list|)
throws|throws
name|XAException
block|{
name|transactionContext
operator|.
name|forget
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getTransactionTimeout
parameter_list|()
throws|throws
name|XAException
block|{
return|return
name|transactionContext
operator|.
name|getTransactionTimeout
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isSameRM
parameter_list|(
name|XAResource
name|xaresource
parameter_list|)
throws|throws
name|XAException
block|{
name|boolean
name|isSame
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|xaresource
operator|!=
literal|null
condition|)
block|{
comment|// Do we have to unwrap?
if|if
condition|(
name|xaresource
operator|instanceof
name|LocalAndXATransaction
condition|)
block|{
name|xaresource
operator|=
operator|(
operator|(
name|LocalAndXATransaction
operator|)
name|xaresource
operator|)
operator|.
name|transactionContext
expr_stmt|;
block|}
name|isSame
operator|=
name|transactionContext
operator|.
name|isSameRM
argument_list|(
name|xaresource
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"{} isSameRM({}) = {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|this
block|,
name|xaresource
block|,
name|isSame
block|}
argument_list|)
expr_stmt|;
return|return
name|isSame
return|;
block|}
specifier|public
name|int
name|prepare
parameter_list|(
name|Xid
name|arg0
parameter_list|)
throws|throws
name|XAException
block|{
return|return
name|transactionContext
operator|.
name|prepare
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|Xid
index|[]
name|recover
parameter_list|(
name|int
name|arg0
parameter_list|)
throws|throws
name|XAException
block|{
name|Xid
index|[]
name|answer
init|=
literal|null
decl_stmt|;
name|answer
operator|=
name|transactionContext
operator|.
name|recover
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"{} recover({}) = {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|this
block|,
name|arg0
block|,
name|answer
block|}
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|void
name|rollback
parameter_list|(
name|Xid
name|arg0
parameter_list|)
throws|throws
name|XAException
block|{
name|transactionContext
operator|.
name|rollback
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|setTransactionTimeout
parameter_list|(
name|int
name|arg0
parameter_list|)
throws|throws
name|XAException
block|{
return|return
name|transactionContext
operator|.
name|setTransactionTimeout
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|void
name|start
parameter_list|(
name|Xid
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
throws|throws
name|XAException
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"{} start {} with {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|this
block|,
name|arg0
block|,
name|arg1
block|}
argument_list|)
expr_stmt|;
name|transactionContext
operator|.
name|start
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
try|try
block|{
name|setInManagedTx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|XAException
operator|)
operator|new
name|XAException
argument_list|(
name|XAException
operator|.
name|XAER_PROTO
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|boolean
name|isInManagedTx
parameter_list|()
block|{
return|return
name|inManagedTx
return|;
block|}
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|transactionContext
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|inManagedTx
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

