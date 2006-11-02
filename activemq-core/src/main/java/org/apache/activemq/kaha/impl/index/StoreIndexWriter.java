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
name|index
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
name|impl
operator|.
name|data
operator|.
name|DataManager
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
name|DataByteArrayOutputStream
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
specifier|final
name|DataByteArrayOutputStream
name|dataOut
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|RandomAccessFile
name|file
decl_stmt|;
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
specifier|protected
specifier|final
name|DataManager
name|redoLog
decl_stmt|;
comment|/**      * Construct a Store index writer      *       * @param file      */
name|StoreIndexWriter
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
block|{
name|this
argument_list|(
name|file
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StoreIndexWriter
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|,
name|String
name|indexName
parameter_list|,
name|DataManager
name|redoLog
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
name|name
operator|=
name|indexName
expr_stmt|;
name|this
operator|.
name|redoLog
operator|=
name|redoLog
expr_stmt|;
block|}
name|void
name|storeItem
parameter_list|(
name|IndexItem
name|indexItem
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|redoLog
operator|!=
literal|null
condition|)
block|{
name|RedoStoreIndexItem
name|redo
init|=
operator|new
name|RedoStoreIndexItem
argument_list|(
name|name
argument_list|,
name|indexItem
operator|.
name|getOffset
argument_list|()
argument_list|,
name|indexItem
argument_list|)
decl_stmt|;
name|redoLog
operator|.
name|storeRedoItem
argument_list|(
name|redo
argument_list|)
expr_stmt|;
block|}
name|dataOut
operator|.
name|reset
argument_list|()
expr_stmt|;
name|indexItem
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
name|indexItem
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
name|void
name|updateIndexes
parameter_list|(
name|IndexItem
name|indexItem
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|redoLog
operator|!=
literal|null
condition|)
block|{
name|RedoStoreIndexItem
name|redo
init|=
operator|new
name|RedoStoreIndexItem
argument_list|(
name|name
argument_list|,
name|indexItem
operator|.
name|getOffset
argument_list|()
argument_list|,
name|indexItem
argument_list|)
decl_stmt|;
name|redoLog
operator|.
name|storeRedoItem
argument_list|(
name|redo
argument_list|)
expr_stmt|;
block|}
name|dataOut
operator|.
name|reset
argument_list|()
expr_stmt|;
name|indexItem
operator|.
name|updateIndexes
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|indexItem
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
name|INDEXES_ONLY_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|redoStoreItem
parameter_list|(
name|RedoStoreIndexItem
name|redo
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|reset
argument_list|()
expr_stmt|;
name|redo
operator|.
name|getIndexItem
argument_list|()
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
name|redo
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

