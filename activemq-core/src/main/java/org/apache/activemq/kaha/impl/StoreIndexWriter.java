begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
end_comment

begin_comment
comment|/**  * Optimized writes to a RandomAcessFile  *   * @version $Revision: 1.1.1.1 $  */
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

begin_comment
comment|/**  * Optimized Store writer  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
class|class
name|StoreIndexWriter
block|{
specifier|protected
name|StoreByteArrayOutputStream
name|dataOut
decl_stmt|;
specifier|protected
name|RandomAccessFile
name|file
decl_stmt|;
comment|/**      * Construct a Store index writer      *       * @param file      */
name|StoreIndexWriter
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|dataOut
operator|=
operator|new
name|StoreByteArrayOutputStream
argument_list|()
expr_stmt|;
block|}
name|void
name|storeItem
parameter_list|(
name|IndexItem
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|reset
argument_list|()
expr_stmt|;
name|index
operator|.
name|write
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|index
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|write
argument_list|(
name|dataOut
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|IndexItem
operator|.
name|INDEX_SIZE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

