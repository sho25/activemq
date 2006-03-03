begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|policy
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
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|util
operator|.
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * A strategy for evicting messages from slow consumers.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageEvictionStrategy
block|{
comment|/**      * Find the message reference in the given list with oldest messages at the front and newer messages at the end      *       * @return the message that has been evicted.      * @throws IOException if an exception occurs such as reading a message content (but should not ever happen      * as usually all the messages will be in RAM when this method is called).      */
name|MessageReference
name|evictMessage
parameter_list|(
name|LinkedList
name|messages
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

