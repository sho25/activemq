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
name|store
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|ConnectionContext
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
name|command
operator|.
name|MessageId
import|;
end_import

begin_comment
comment|/**  * Represents a message store which is used by the persistent   * implementations  *   * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|ReferenceStore
extends|extends
name|MessageStore
block|{
specifier|public
class|class
name|ReferenceData
block|{
name|long
name|expiration
decl_stmt|;
name|int
name|fileId
decl_stmt|;
name|int
name|offset
decl_stmt|;
specifier|public
name|long
name|getExpiration
parameter_list|()
block|{
return|return
name|expiration
return|;
block|}
specifier|public
name|void
name|setExpiration
parameter_list|(
name|long
name|expiration
parameter_list|)
block|{
name|this
operator|.
name|expiration
operator|=
name|expiration
expr_stmt|;
block|}
specifier|public
name|int
name|getFileId
parameter_list|()
block|{
return|return
name|fileId
return|;
block|}
specifier|public
name|void
name|setFileId
parameter_list|(
name|int
name|file
parameter_list|)
block|{
name|this
operator|.
name|fileId
operator|=
name|file
expr_stmt|;
block|}
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
specifier|public
name|void
name|setOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ReferenceData fileId="
operator|+
name|fileId
operator|+
literal|", offset="
operator|+
name|offset
operator|+
literal|", expiration="
operator|+
name|expiration
return|;
block|}
block|}
comment|/**      * Adds a message reference to the message store      */
specifier|public
name|void
name|addMessageReference
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageId
name|messageId
parameter_list|,
name|ReferenceData
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Looks up a message using either the String messageID or the messageNumber. Implementations are encouraged to fill      * in the missing key if its easy to do so.      */
specifier|public
name|ReferenceData
name|getMessageReference
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

