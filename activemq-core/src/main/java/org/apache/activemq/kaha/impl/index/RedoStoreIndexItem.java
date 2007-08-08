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
name|Externalizable
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
name|io
operator|.
name|ObjectInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutput
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

begin_class
specifier|public
class|class
name|RedoStoreIndexItem
implements|implements
name|Externalizable
block|{
specifier|public
specifier|static
specifier|final
name|Marshaller
name|MARSHALLER
init|=
operator|new
name|Marshaller
argument_list|()
block|{
specifier|public
name|Object
name|readPayload
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|RedoStoreIndexItem
name|item
init|=
operator|new
name|RedoStoreIndexItem
argument_list|()
decl_stmt|;
name|item
operator|.
name|readExternal
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|item
return|;
block|}
specifier|public
name|void
name|writePayload
parameter_list|(
name|Object
name|object
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|RedoStoreIndexItem
name|item
init|=
operator|(
name|RedoStoreIndexItem
operator|)
name|object
decl_stmt|;
name|item
operator|.
name|writeExternal
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4865508871719676655L
decl_stmt|;
specifier|private
name|String
name|indexName
decl_stmt|;
specifier|private
name|IndexItem
name|indexItem
decl_stmt|;
specifier|private
name|long
name|offset
decl_stmt|;
specifier|public
name|RedoStoreIndexItem
parameter_list|()
block|{     }
specifier|public
name|RedoStoreIndexItem
parameter_list|(
name|String
name|indexName
parameter_list|,
name|long
name|offset
parameter_list|,
name|IndexItem
name|item
parameter_list|)
block|{
name|this
operator|.
name|indexName
operator|=
name|indexName
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|indexItem
operator|=
name|item
expr_stmt|;
block|}
specifier|public
name|void
name|readExternal
parameter_list|(
name|ObjectInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|readExternal
argument_list|(
operator|(
name|DataInput
operator|)
name|in
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|readExternal
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
comment|// indexName = in.readUTF();
name|offset
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|indexItem
operator|=
operator|new
name|IndexItem
argument_list|()
expr_stmt|;
name|indexItem
operator|.
name|read
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|writeExternal
parameter_list|(
name|ObjectOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|writeExternal
argument_list|(
operator|(
name|DataOutput
operator|)
name|out
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|writeExternal
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
comment|// out.writeUTF(indexName);
name|out
operator|.
name|writeLong
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|indexItem
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
name|indexName
return|;
block|}
specifier|public
name|void
name|setIndexName
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
name|this
operator|.
name|indexName
operator|=
name|indexName
expr_stmt|;
block|}
specifier|public
name|IndexItem
name|getIndexItem
parameter_list|()
block|{
return|return
name|indexItem
return|;
block|}
specifier|public
name|void
name|setIndexItem
parameter_list|(
name|IndexItem
name|item
parameter_list|)
block|{
name|this
operator|.
name|indexItem
operator|=
name|item
expr_stmt|;
block|}
specifier|public
name|long
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
name|long
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
block|}
end_class

end_unit

