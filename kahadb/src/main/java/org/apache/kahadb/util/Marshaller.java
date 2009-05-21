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
name|kahadb
operator|.
name|util
package|;
end_package

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
name|DataOutput
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

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|Marshaller
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * Write the payload of the object to the DataOutput stream.      *       * @param object       * @param dataOut      * @throws IOException      */
name|void
name|writePayload
parameter_list|(
name|T
name|object
parameter_list|,
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Read the payload of the object from the DataInput stream.      *       * @param dataIn       * @return unmarshalled object      * @throws IOException      */
name|T
name|readPayload
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**       * @return -1 if the object do not always marshall to a fixed size, otherwise return that fixed size.      */
name|int
name|getFixedSize
parameter_list|()
function_decl|;
comment|/**      *       * @return true if the {@link #deepCopy(Object)} operations is supported.      */
name|boolean
name|isDeepCopySupported
parameter_list|()
function_decl|;
comment|/**      * @return a deep copy of the source object.      */
name|T
name|deepCopy
parameter_list|(
name|T
name|source
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

