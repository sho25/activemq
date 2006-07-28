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
name|kaha
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
comment|/**  * Implementation of a Marshaller for Strings  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|StringMarshaller
implements|implements
name|Marshaller
block|{
comment|/**      * Write the payload of this entry to the RawContainer      *       * @param object      * @param dataOut      * @throws IOException      */
specifier|public
name|void
name|writePayload
parameter_list|(
name|Object
name|object
parameter_list|,
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|writeUTF
argument_list|(
name|object
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read the entry from the RawContainer      *       * @param dataIn      * @return unmarshalled object      * @throws IOException      */
specifier|public
name|Object
name|readPayload
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dataIn
operator|.
name|readUTF
argument_list|()
return|;
block|}
block|}
end_class

end_unit

