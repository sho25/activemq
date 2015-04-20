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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|journal
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
name|store
operator|.
name|kahadb
operator|.
name|KahaDBStore
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
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Created by ceposta  *<a href="http://christianposta.com/blog>http://christianposta.com/blog</a>.  */
end_comment

begin_class
specifier|public
class|class
name|PreallocationJournalTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PreallocationJournalTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testSparseFilePreallocation
parameter_list|()
throws|throws
name|Exception
block|{
name|executeTest
argument_list|(
literal|"sparse_file"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOSCopyPreallocation
parameter_list|()
throws|throws
name|Exception
block|{
name|executeTest
argument_list|(
literal|"os_kernel_copy"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testZerosPreallocation
parameter_list|()
throws|throws
name|Exception
block|{
name|executeTest
argument_list|(
literal|"zeros"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|executeTest
parameter_list|(
name|String
name|preallocationStrategy
parameter_list|)
throws|throws
name|Exception
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|randInt
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|File
name|dataDirectory
init|=
operator|new
name|File
argument_list|(
literal|"./target/activemq-data/kahadb"
operator|+
name|randInt
argument_list|)
decl_stmt|;
name|KahaDBStore
name|store
init|=
operator|new
name|KahaDBStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
name|dataDirectory
argument_list|)
expr_stmt|;
name|store
operator|.
name|setPreallocationStrategy
argument_list|(
name|preallocationStrategy
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|File
name|journalLog
init|=
operator|new
name|File
argument_list|(
name|dataDirectory
argument_list|,
literal|"db-1.log"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"file exists"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|journalLog
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|journalLog
argument_list|)
decl_stmt|;
specifier|final
name|FileChannel
name|channel
init|=
name|is
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"file size as expected"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"file size:"
operator|+
name|journalLog
operator|+
literal|", chan.size "
operator|+
name|channel
operator|.
name|size
argument_list|()
operator|+
literal|", jfileSize.length: "
operator|+
name|journalLog
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Journal
operator|.
name|DEFAULT_MAX_FILE_LENGTH
operator|==
name|channel
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|channel
operator|.
name|position
argument_list|(
literal|1
operator|*
literal|1024
operator|*
literal|1024
operator|+
literal|1
argument_list|)
expr_stmt|;
name|ByteBuffer
name|buff
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|channel
operator|.
name|read
argument_list|(
name|buff
argument_list|)
expr_stmt|;
name|buff
operator|.
name|flip
argument_list|()
expr_stmt|;
name|buff
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x00
argument_list|,
name|buff
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"File size: "
operator|+
name|channel
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

