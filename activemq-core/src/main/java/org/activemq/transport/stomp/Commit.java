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
name|transport
operator|.
name|stomp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|TransactionId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|TransactionInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

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
name|java
operator|.
name|net
operator|.
name|ProtocolException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
class|class
name|Commit
implements|implements
name|StompCommand
block|{
specifier|private
name|StompWireFormat
name|format
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|HeaderParser
name|parser
init|=
operator|new
name|HeaderParser
argument_list|()
decl_stmt|;
name|Commit
parameter_list|(
name|StompWireFormat
name|format
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
specifier|public
name|CommandEnvelope
name|build
parameter_list|(
name|String
name|commandLine
parameter_list|,
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Properties
name|headers
init|=
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|)
decl_stmt|;
while|while
condition|(
name|in
operator|.
name|readByte
argument_list|()
operator|!=
literal|0
condition|)
block|{         }
name|String
name|user_tx_id
init|=
name|headers
operator|.
name|getProperty
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSACTION
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|headers
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSACTION
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"Must specify the transaction you are committing"
argument_list|)
throw|;
block|}
name|TransactionId
name|tx_id
init|=
name|format
operator|.
name|getTransactionId
argument_list|(
name|user_tx_id
argument_list|)
decl_stmt|;
name|TransactionInfo
name|tx
init|=
operator|new
name|TransactionInfo
argument_list|()
decl_stmt|;
name|tx
operator|.
name|setTransactionId
argument_list|(
name|tx_id
argument_list|)
expr_stmt|;
name|tx
operator|.
name|setType
argument_list|(
name|TransactionInfo
operator|.
name|COMMIT_ONE_PHASE
argument_list|)
expr_stmt|;
name|format
operator|.
name|clearTransactionId
argument_list|(
name|user_tx_id
argument_list|)
expr_stmt|;
return|return
operator|new
name|CommandEnvelope
argument_list|(
name|tx
argument_list|,
name|headers
argument_list|)
return|;
block|}
block|}
end_class

end_unit

