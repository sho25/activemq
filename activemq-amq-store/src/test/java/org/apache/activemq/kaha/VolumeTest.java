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
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|VolumeTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|NUMBER
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|VolumeTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Store
name|store
decl_stmt|;
specifier|protected
name|String
name|name
decl_stmt|;
comment|/*      * dump a large number of messages into a list - then retreive them      */
specifier|public
name|void
name|testListVolume
parameter_list|()
throws|throws
name|Exception
block|{
name|ListContainer
name|container
init|=
name|store
operator|.
name|getListContainer
argument_list|(
literal|"volume"
argument_list|)
decl_stmt|;
name|container
operator|.
name|setMarshaller
argument_list|(
name|Store
operator|.
name|BYTES_MARSHALLER
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUMBER
condition|;
name|i
operator|++
control|)
block|{
name|container
operator|.
name|add
argument_list|(
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|100000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"persisted "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|container
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|assertNotNull
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|%
literal|100000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"retrived  "
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Different retrieved to stored"
argument_list|,
name|NUMBER
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Store
name|getStore
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|StoreFactory
operator|.
name|open
argument_list|(
name|name
argument_list|,
literal|"rw"
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|name
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"basedir"
argument_list|,
literal|"."
argument_list|)
operator|+
literal|"/target/activemq-data/volume-container.db"
expr_stmt|;
name|StoreFactory
operator|.
name|delete
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|store
operator|=
name|StoreFactory
operator|.
name|open
argument_list|(
name|name
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|StoreFactory
operator|.
name|delete
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
