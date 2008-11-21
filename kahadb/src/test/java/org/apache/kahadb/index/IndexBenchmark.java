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
name|index
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|CountDownLatch
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
name|AtomicBoolean
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
name|AtomicLong
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
name|apache
operator|.
name|kahadb
operator|.
name|page
operator|.
name|PageFile
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|page
operator|.
name|Transaction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|util
operator|.
name|IOHelper
import|;
end_import

begin_comment
comment|/**  * @author chirino  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|IndexBenchmark
extends|extends
name|TestCase
block|{
comment|// Slower machines might need to make this bigger.
specifier|private
specifier|static
specifier|final
name|long
name|SAMPLE_DURATION
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"SAMPLES_DURATION"
argument_list|,
literal|""
operator|+
literal|1000
operator|*
literal|5
argument_list|)
argument_list|)
decl_stmt|;
comment|// How many times do we sample?
specifier|private
specifier|static
specifier|final
name|long
name|SAMPLES
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"SAMPLES"
argument_list|,
literal|""
operator|+
literal|60
operator|*
literal|1000
operator|/
name|SAMPLE_DURATION
argument_list|)
argument_list|)
decl_stmt|;
comment|// How many indexes will we be benchmarking concurrently?
specifier|private
specifier|static
specifier|final
name|int
name|INDEX_COUNT
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"INDEX_COUNT"
argument_list|,
literal|""
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// Indexes tend to perform worse when they get big.. so how many items
comment|// should we put into the index before we start sampling.
specifier|private
specifier|static
specifier|final
name|int
name|INDEX_PRE_LOAD_COUNT
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"INDEX_PRE_LOAD_COUNT"
argument_list|,
literal|""
operator|+
literal|10000
operator|/
name|INDEX_COUNT
argument_list|)
argument_list|)
decl_stmt|;
specifier|protected
name|File
name|ROOT_DIR
decl_stmt|;
specifier|protected
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|indexes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|PageFile
name|pf
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ROOT_DIR
operator|=
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|ROOT_DIR
argument_list|)
expr_stmt|;
name|IOHelper
operator|.
name|deleteChildren
argument_list|(
name|ROOT_DIR
argument_list|)
expr_stmt|;
name|pf
operator|=
operator|new
name|PageFile
argument_list|(
name|ROOT_DIR
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|pf
operator|.
name|load
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
for|for
control|(
name|Index
name|i
range|:
name|indexes
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|i
operator|.
name|unload
argument_list|(
name|tx
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|abstract
specifier|protected
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|createIndex
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|synchronized
specifier|private
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|openIndex
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
init|=
name|indexes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
name|index
operator|=
name|createIndex
argument_list|()
expr_stmt|;
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|indexes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|index
return|;
block|}
class|class
name|Producer
extends|extends
name|Thread
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|AtomicBoolean
name|shutdown
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|public
name|Producer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
literal|"Producer: "
operator|+
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|shutdown
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
init|=
name|openIndex
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|long
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|shutdown
operator|.
name|get
argument_list|()
condition|)
block|{
name|long
name|c
init|=
name|counter
decl_stmt|;
name|String
name|key
init|=
name|key
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|index
operator|.
name|put
argument_list|(
name|tx
argument_list|,
name|key
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
comment|// This avoids consumer starvation..
name|onProduced
argument_list|(
name|counter
operator|++
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onProduced
parameter_list|(
name|long
name|counter
parameter_list|)
block|{         }
block|}
specifier|protected
name|String
name|key
parameter_list|(
name|long
name|c
parameter_list|)
block|{
return|return
literal|"a-long-message-id-like-key-"
operator|+
name|c
return|;
block|}
class|class
name|Consumer
extends|extends
name|Thread
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|AtomicBoolean
name|shutdown
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|public
name|Consumer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
literal|"Consumer: "
operator|+
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|shutdown
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Transaction
name|tx
init|=
name|pf
operator|.
name|tx
argument_list|()
decl_stmt|;
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
init|=
name|openIndex
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|long
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|shutdown
operator|.
name|get
argument_list|()
condition|)
block|{
name|long
name|c
init|=
name|counter
decl_stmt|;
name|String
name|key
init|=
name|key
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|Long
name|record
init|=
name|index
operator|.
name|get
argument_list|(
name|tx
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|record
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|index
operator|.
name|remove
argument_list|(
name|tx
argument_list|,
name|key
argument_list|)
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Remove failed..."
argument_list|)
expr_stmt|;
block|}
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
name|onConsumed
argument_list|(
name|counter
operator|++
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onConsumed
parameter_list|(
name|long
name|counter
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|dumpIndex
parameter_list|(
name|Index
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|index
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|testLoad
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Producer
name|producers
index|[]
init|=
operator|new
name|Producer
index|[
name|INDEX_COUNT
index|]
decl_stmt|;
specifier|final
name|Consumer
name|consumers
index|[]
init|=
operator|new
name|Consumer
index|[
name|INDEX_COUNT
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|preloadCountDown
init|=
operator|new
name|CountDownLatch
argument_list|(
name|INDEX_COUNT
argument_list|)
decl_stmt|;
specifier|final
name|AtomicLong
name|producedRecords
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|consumedRecords
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting: "
operator|+
name|INDEX_COUNT
operator|+
literal|" producers"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|INDEX_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|producers
index|[
name|i
index|]
operator|=
operator|new
name|Producer
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
block|{
specifier|private
name|boolean
name|prelaodDone
decl_stmt|;
specifier|public
name|void
name|onProduced
parameter_list|(
name|long
name|counter
parameter_list|)
block|{
if|if
condition|(
operator|!
name|prelaodDone
operator|&&
name|counter
operator|>=
name|INDEX_PRE_LOAD_COUNT
condition|)
block|{
name|prelaodDone
operator|=
literal|true
expr_stmt|;
name|preloadCountDown
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
name|producedRecords
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|producers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for each producer create "
operator|+
name|INDEX_PRE_LOAD_COUNT
operator|+
literal|" records before starting the consumers."
argument_list|)
expr_stmt|;
name|preloadCountDown
operator|.
name|await
argument_list|()
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Preloaded "
operator|+
name|INDEX_PRE_LOAD_COUNT
operator|*
name|INDEX_COUNT
operator|+
literal|" records at "
operator|+
operator|(
name|INDEX_PRE_LOAD_COUNT
operator|*
name|INDEX_COUNT
operator|*
literal|1000f
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|+
literal|" records/sec"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting: "
operator|+
name|INDEX_COUNT
operator|+
literal|" consumers"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|INDEX_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|consumers
index|[
name|i
index|]
operator|=
operator|new
name|Consumer
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
block|{
specifier|public
name|void
name|onConsumed
parameter_list|(
name|long
name|counter
parameter_list|)
block|{
name|consumedRecords
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|consumers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|long
name|sample_start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Taking "
operator|+
name|SAMPLES
operator|+
literal|" performance samples every "
operator|+
name|SAMPLE_DURATION
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"time (s), produced, produce rate (r/s), consumed, consume rate (r/s), used memory (k)"
argument_list|)
expr_stmt|;
name|producedRecords
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|consumedRecords
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|SAMPLES
condition|;
name|i
operator|++
control|)
block|{
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|SAMPLE_DURATION
argument_list|)
expr_stmt|;
name|end
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|long
name|p
init|=
name|producedRecords
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|c
init|=
name|consumedRecords
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|usedMemory
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
operator|-
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|freeMemory
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
operator|(
name|end
operator|-
name|sample_start
operator|)
operator|/
literal|1000f
operator|)
operator|+
literal|", "
operator|+
name|p
operator|+
literal|", "
operator|+
operator|(
name|p
operator|*
literal|1000f
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|+
literal|", "
operator|+
name|c
operator|+
literal|", "
operator|+
operator|(
name|c
operator|*
literal|1000f
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|+
literal|", "
operator|+
operator|(
name|usedMemory
operator|/
operator|(
literal|1024
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Samples done... Shutting down the producers and consumers..."
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|INDEX_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|producers
index|[
name|i
index|]
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|consumers
index|[
name|i
index|]
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|INDEX_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|producers
index|[
name|i
index|]
operator|.
name|join
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|consumers
index|[
name|i
index|]
operator|.
name|join
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Shutdown."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

