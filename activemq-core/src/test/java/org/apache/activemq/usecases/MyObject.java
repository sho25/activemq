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
name|usecases
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
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
name|ObjectStreamException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
specifier|public
class|class
name|MyObject
implements|implements
name|Serializable
block|{
specifier|private
name|String
name|message
decl_stmt|;
specifier|private
name|AtomicInteger
name|writeObjectCalled
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|AtomicInteger
name|readObjectCalled
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|AtomicInteger
name|readObjectNoDataCalled
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|public
name|MyObject
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
specifier|private
name|void
name|writeObject
parameter_list|(
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|writeObjectCalled
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|out
operator|.
name|defaultWriteObject
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|readObject
parameter_list|(
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|in
operator|.
name|defaultReadObject
argument_list|()
expr_stmt|;
name|readObjectCalled
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|readObjectNoData
parameter_list|()
throws|throws
name|ObjectStreamException
block|{
name|readObjectNoDataCalled
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getWriteObjectCalled
parameter_list|()
block|{
return|return
name|writeObjectCalled
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|int
name|getReadObjectCalled
parameter_list|()
block|{
return|return
name|readObjectCalled
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|int
name|getReadObjectNoDataCalled
parameter_list|()
block|{
return|return
name|readObjectNoDataCalled
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

