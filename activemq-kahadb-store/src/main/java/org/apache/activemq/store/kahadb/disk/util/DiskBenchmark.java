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
name|util
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
name|util
operator|.
name|RecoverableRandomAccessFile
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * This class is used to get a benchmark the raw disk performance.  */
end_comment

begin_class
specifier|public
class|class
name|DiskBenchmark
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|SKIP_METADATA_UPDATE
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"org.apache.activemq.file.skipMetadataUpdate"
argument_list|)
decl_stmt|;
name|boolean
name|verbose
decl_stmt|;
comment|// reads and writes work with 4k of data at a time.
name|int
name|bs
init|=
literal|1024
operator|*
literal|4
decl_stmt|;
comment|// Work with 100 meg file.
name|long
name|size
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|500
decl_stmt|;
name|long
name|sampleInterval
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|DiskBenchmark
name|benchmark
init|=
operator|new
name|DiskBenchmark
argument_list|()
decl_stmt|;
name|args
operator|=
name|CommandLineSupport
operator|.
name|setOptions
argument_list|(
name|benchmark
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
literal|"disk-benchmark.dat"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|files
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|f
range|:
name|files
control|)
block|{
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"File "
operator|+
name|file
operator|+
literal|" allready exists, will not benchmark."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Benchmarking: "
operator|+
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|Report
name|report
init|=
name|benchmark
operator|.
name|benchmark
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|report
operator|.
name|toString
argument_list|()
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
if|if
condition|(
name|benchmark
operator|.
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR:"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
class|class
name|Report
block|{
specifier|public
name|int
name|size
decl_stmt|;
specifier|public
name|int
name|writes
decl_stmt|;
specifier|public
name|long
name|writeDuration
decl_stmt|;
specifier|public
name|int
name|syncWrites
decl_stmt|;
specifier|public
name|long
name|syncWriteDuration
decl_stmt|;
specifier|public
name|int
name|reads
decl_stmt|;
specifier|public
name|long
name|readDuration
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Writes: \n"
operator|+
literal|"  "
operator|+
name|writes
operator|+
literal|" writes of size "
operator|+
name|size
operator|+
literal|" written in "
operator|+
operator|(
name|writeDuration
operator|/
literal|1000.0
operator|)
operator|+
literal|" seconds.\n"
operator|+
literal|"  "
operator|+
name|getWriteRate
argument_list|()
operator|+
literal|" writes/second.\n"
operator|+
literal|"  "
operator|+
name|getWriteSizeRate
argument_list|()
operator|+
literal|" megs/second.\n"
operator|+
literal|"\n"
operator|+
literal|"Sync Writes: \n"
operator|+
literal|"  "
operator|+
name|syncWrites
operator|+
literal|" writes of size "
operator|+
name|size
operator|+
literal|" written in "
operator|+
operator|(
name|syncWriteDuration
operator|/
literal|1000.0
operator|)
operator|+
literal|" seconds.\n"
operator|+
literal|"  "
operator|+
name|getSyncWriteRate
argument_list|()
operator|+
literal|" writes/second.\n"
operator|+
literal|"  "
operator|+
name|getSyncWriteSizeRate
argument_list|()
operator|+
literal|" megs/second.\n"
operator|+
literal|"\n"
operator|+
literal|"Reads: \n"
operator|+
literal|"  "
operator|+
name|reads
operator|+
literal|" reads of size "
operator|+
name|size
operator|+
literal|" read in "
operator|+
operator|(
name|readDuration
operator|/
literal|1000.0
operator|)
operator|+
literal|" seconds.\n"
operator|+
literal|"  "
operator|+
name|getReadRate
argument_list|()
operator|+
literal|" writes/second.\n"
operator|+
literal|"  "
operator|+
name|getReadSizeRate
argument_list|()
operator|+
literal|" megs/second.\n"
operator|+
literal|"\n"
operator|+
literal|""
return|;
block|}
specifier|private
name|float
name|getWriteSizeRate
parameter_list|()
block|{
name|float
name|rc
init|=
name|writes
decl_stmt|;
name|rc
operator|*=
name|size
expr_stmt|;
name|rc
operator|/=
operator|(
literal|1024
operator|*
literal|1024
operator|)
expr_stmt|;
comment|// put it in megs
name|rc
operator|/=
operator|(
name|writeDuration
operator|/
literal|1000.0
operator|)
expr_stmt|;
comment|// get rate.
return|return
name|rc
return|;
block|}
specifier|private
name|float
name|getWriteRate
parameter_list|()
block|{
name|float
name|rc
init|=
name|writes
decl_stmt|;
name|rc
operator|/=
operator|(
name|writeDuration
operator|/
literal|1000.0
operator|)
expr_stmt|;
comment|// get rate.
return|return
name|rc
return|;
block|}
specifier|private
name|float
name|getSyncWriteSizeRate
parameter_list|()
block|{
name|float
name|rc
init|=
name|syncWrites
decl_stmt|;
name|rc
operator|*=
name|size
expr_stmt|;
name|rc
operator|/=
operator|(
literal|1024
operator|*
literal|1024
operator|)
expr_stmt|;
comment|// put it in megs
name|rc
operator|/=
operator|(
name|syncWriteDuration
operator|/
literal|1000.0
operator|)
expr_stmt|;
comment|// get rate.
return|return
name|rc
return|;
block|}
specifier|private
name|float
name|getSyncWriteRate
parameter_list|()
block|{
name|float
name|rc
init|=
name|syncWrites
decl_stmt|;
name|rc
operator|/=
operator|(
name|syncWriteDuration
operator|/
literal|1000.0
operator|)
expr_stmt|;
comment|// get rate.
return|return
name|rc
return|;
block|}
specifier|private
name|float
name|getReadSizeRate
parameter_list|()
block|{
name|float
name|rc
init|=
name|reads
decl_stmt|;
name|rc
operator|*=
name|size
expr_stmt|;
name|rc
operator|/=
operator|(
literal|1024
operator|*
literal|1024
operator|)
expr_stmt|;
comment|// put it in megs
name|rc
operator|/=
operator|(
name|readDuration
operator|/
literal|1000.0
operator|)
expr_stmt|;
comment|// get rate.
return|return
name|rc
return|;
block|}
specifier|private
name|float
name|getReadRate
parameter_list|()
block|{
name|float
name|rc
init|=
name|reads
decl_stmt|;
name|rc
operator|/=
operator|(
name|readDuration
operator|/
literal|1000.0
operator|)
expr_stmt|;
comment|// get rate.
return|return
name|rc
return|;
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|void
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
specifier|public
name|int
name|getWrites
parameter_list|()
block|{
return|return
name|writes
return|;
block|}
specifier|public
name|void
name|setWrites
parameter_list|(
name|int
name|writes
parameter_list|)
block|{
name|this
operator|.
name|writes
operator|=
name|writes
expr_stmt|;
block|}
specifier|public
name|long
name|getWriteDuration
parameter_list|()
block|{
return|return
name|writeDuration
return|;
block|}
specifier|public
name|void
name|setWriteDuration
parameter_list|(
name|long
name|writeDuration
parameter_list|)
block|{
name|this
operator|.
name|writeDuration
operator|=
name|writeDuration
expr_stmt|;
block|}
specifier|public
name|int
name|getSyncWrites
parameter_list|()
block|{
return|return
name|syncWrites
return|;
block|}
specifier|public
name|void
name|setSyncWrites
parameter_list|(
name|int
name|syncWrites
parameter_list|)
block|{
name|this
operator|.
name|syncWrites
operator|=
name|syncWrites
expr_stmt|;
block|}
specifier|public
name|long
name|getSyncWriteDuration
parameter_list|()
block|{
return|return
name|syncWriteDuration
return|;
block|}
specifier|public
name|void
name|setSyncWriteDuration
parameter_list|(
name|long
name|syncWriteDuration
parameter_list|)
block|{
name|this
operator|.
name|syncWriteDuration
operator|=
name|syncWriteDuration
expr_stmt|;
block|}
specifier|public
name|int
name|getReads
parameter_list|()
block|{
return|return
name|reads
return|;
block|}
specifier|public
name|void
name|setReads
parameter_list|(
name|int
name|reads
parameter_list|)
block|{
name|this
operator|.
name|reads
operator|=
name|reads
expr_stmt|;
block|}
specifier|public
name|long
name|getReadDuration
parameter_list|()
block|{
return|return
name|readDuration
return|;
block|}
specifier|public
name|void
name|setReadDuration
parameter_list|(
name|long
name|readDuration
parameter_list|)
block|{
name|this
operator|.
name|readDuration
operator|=
name|readDuration
expr_stmt|;
block|}
block|}
specifier|public
name|Report
name|benchmark
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|Report
name|rc
init|=
operator|new
name|Report
argument_list|()
decl_stmt|;
comment|// Initialize the block we will be writing to disk.
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|bs
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|'a'
operator|+
operator|(
name|i
operator|%
literal|26
operator|)
argument_list|)
expr_stmt|;
block|}
name|rc
operator|.
name|size
operator|=
name|data
operator|.
name|length
expr_stmt|;
name|RecoverableRandomAccessFile
name|raf
init|=
operator|new
name|RecoverableRandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
comment|//        RandomAccessFile raf = new RandomAccessFile(file, "rw");
name|preallocateDataFile
argument_list|(
name|raf
argument_list|,
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
comment|// Figure out how many writes we can do in the sample interval.
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|ioCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|(
name|now
operator|-
name|start
operator|)
operator|>
name|sampleInterval
condition|)
block|{
break|break;
block|}
name|raf
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|+
name|data
operator|.
name|length
operator|<
name|size
condition|;
name|i
operator|+=
name|data
operator|.
name|length
control|)
block|{
name|raf
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|ioCount
operator|++
expr_stmt|;
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|now
operator|-
name|start
operator|)
operator|>
name|sampleInterval
condition|)
block|{
break|break;
block|}
block|}
comment|// Sync to disk so that the we actually write the data to disk..
comment|// otherwise OS buffering might not really do the write.
name|raf
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
operator|!
name|SKIP_METADATA_UPDATE
argument_list|)
expr_stmt|;
block|}
name|raf
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
operator|!
name|SKIP_METADATA_UPDATE
argument_list|)
expr_stmt|;
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|rc
operator|.
name|size
operator|=
name|data
operator|.
name|length
expr_stmt|;
name|rc
operator|.
name|writes
operator|=
name|ioCount
expr_stmt|;
name|rc
operator|.
name|writeDuration
operator|=
operator|(
name|now
operator|-
name|start
operator|)
expr_stmt|;
name|raf
operator|=
operator|new
name|RecoverableRandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|ioCount
operator|=
literal|0
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|(
name|now
operator|-
name|start
operator|)
operator|>
name|sampleInterval
condition|)
block|{
break|break;
block|}
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|+
name|data
operator|.
name|length
operator|<
name|size
condition|;
name|i
operator|+=
name|data
operator|.
name|length
control|)
block|{
name|raf
operator|.
name|seek
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|raf
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|raf
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ioCount
operator|++
expr_stmt|;
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|now
operator|-
name|start
operator|)
operator|>
name|sampleInterval
condition|)
block|{
break|break;
block|}
block|}
block|}
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|rc
operator|.
name|syncWrites
operator|=
name|ioCount
expr_stmt|;
name|rc
operator|.
name|syncWriteDuration
operator|=
operator|(
name|now
operator|-
name|start
operator|)
expr_stmt|;
name|raf
operator|=
operator|new
name|RecoverableRandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|ioCount
operator|=
literal|0
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|(
name|now
operator|-
name|start
operator|)
operator|>
name|sampleInterval
condition|)
block|{
break|break;
block|}
name|raf
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|+
name|data
operator|.
name|length
operator|<
name|size
condition|;
name|i
operator|+=
name|data
operator|.
name|length
control|)
block|{
name|raf
operator|.
name|seek
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|raf
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|ioCount
operator|++
expr_stmt|;
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|now
operator|-
name|start
operator|)
operator|>
name|sampleInterval
condition|)
block|{
break|break;
block|}
block|}
block|}
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
name|rc
operator|.
name|reads
operator|=
name|ioCount
expr_stmt|;
name|rc
operator|.
name|readDuration
operator|=
operator|(
name|now
operator|-
name|start
operator|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|private
name|void
name|preallocateDataFile
parameter_list|(
name|RecoverableRandomAccessFile
name|raf
parameter_list|,
name|File
name|location
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|tmpFile
decl_stmt|;
if|if
condition|(
name|location
operator|!=
literal|null
operator|&&
name|location
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|tmpFile
operator|=
operator|new
name|File
argument_list|(
name|location
argument_list|,
literal|"template.dat"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tmpFile
operator|=
operator|new
name|File
argument_list|(
literal|"template.dat"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tmpFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using a template file: "
operator|+
name|tmpFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|RandomAccessFile
name|templateFile
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|tmpFile
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|templateFile
operator|.
name|setLength
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|templateFile
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|templateFile
operator|.
name|getChannel
argument_list|()
operator|.
name|transferTo
argument_list|(
literal|0
argument_list|,
name|size
argument_list|,
name|raf
operator|.
name|getChannel
argument_list|()
argument_list|)
expr_stmt|;
name|templateFile
operator|.
name|close
argument_list|()
expr_stmt|;
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|isVerbose
parameter_list|()
block|{
return|return
name|verbose
return|;
block|}
specifier|public
name|void
name|setVerbose
parameter_list|(
name|boolean
name|verbose
parameter_list|)
block|{
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
block|}
specifier|public
name|int
name|getBs
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
specifier|public
name|void
name|setBs
parameter_list|(
name|int
name|bs
parameter_list|)
block|{
name|this
operator|.
name|bs
operator|=
name|bs
expr_stmt|;
block|}
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
specifier|public
name|long
name|getSampleInterval
parameter_list|()
block|{
return|return
name|sampleInterval
return|;
block|}
specifier|public
name|void
name|setSampleInterval
parameter_list|(
name|long
name|sampleInterval
parameter_list|)
block|{
name|this
operator|.
name|sampleInterval
operator|=
name|sampleInterval
expr_stmt|;
block|}
block|}
end_class

end_unit

