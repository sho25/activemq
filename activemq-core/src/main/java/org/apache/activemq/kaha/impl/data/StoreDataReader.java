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
operator|.
name|impl
operator|.
name|data
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
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|kaha
operator|.
name|Marshaller
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
name|kaha
operator|.
name|StoreLocation
import|;
end_import

begin_comment
comment|/**  * Optimized Store reader  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|final
class|class
name|StoreDataReader
block|{
specifier|private
name|DataManager
name|dataManager
decl_stmt|;
specifier|private
name|StoreByteArrayInputStream
name|dataIn
decl_stmt|;
comment|/**      * Construct a Store reader      *       * @param file      */
name|StoreDataReader
parameter_list|(
name|DataManager
name|fileManager
parameter_list|)
block|{
name|this
operator|.
name|dataManager
operator|=
name|fileManager
expr_stmt|;
name|this
operator|.
name|dataIn
operator|=
operator|new
name|StoreByteArrayInputStream
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets the size property on a DataItem and returns the type of item that this was       * created as.      *       * @param marshaller      * @param item      * @return      * @throws IOException      */
specifier|protected
name|byte
name|readDataItemSize
parameter_list|(
name|DataItem
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|file
init|=
name|dataManager
operator|.
name|getDataFile
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|item
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
comment|// jump to the size field
name|byte
name|rc
init|=
name|file
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|item
operator|.
name|setSize
argument_list|(
name|file
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|protected
name|Object
name|readItem
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|,
name|StoreLocation
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|file
init|=
name|dataManager
operator|.
name|getDataFile
argument_list|(
name|item
argument_list|)
decl_stmt|;
comment|// TODO: we could reuse the buffer in dataIn if it's big enough to avoid
comment|// allocating byte[] arrays on every readItem.
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|item
operator|.
name|getSize
argument_list|()
index|]
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|item
operator|.
name|getOffset
argument_list|()
operator|+
name|DataManager
operator|.
name|ITEM_HEAD_SIZE
argument_list|)
expr_stmt|;
name|file
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|dataIn
operator|.
name|restart
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|marshaller
operator|.
name|readPayload
argument_list|(
name|dataIn
argument_list|)
return|;
block|}
block|}
end_class

end_unit

