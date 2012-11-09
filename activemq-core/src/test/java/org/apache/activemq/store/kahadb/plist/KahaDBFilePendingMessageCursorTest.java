begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|plist
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
name|broker
operator|.
name|BrokerService
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
name|broker
operator|.
name|region
operator|.
name|*
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
name|broker
operator|.
name|region
operator|.
name|cursors
operator|.
name|FilePendingMessageCursor
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
name|broker
operator|.
name|region
operator|.
name|cursors
operator|.
name|FilePendingMessageCursorTestSupport
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
name|command
operator|.
name|ActiveMQMessage
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
name|command
operator|.
name|ActiveMQQueue
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
name|command
operator|.
name|MessageId
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
name|store
operator|.
name|kahadb
operator|.
name|disk
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
name|activemq
operator|.
name|usage
operator|.
name|SystemUsage
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
name|assertFalse
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|KahaDBFilePendingMessageCursorTest
extends|extends
name|FilePendingMessageCursorTestSupport
block|{
annotation|@
name|Test
specifier|public
name|void
name|testAddRemoveAddIndexSize
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|SystemUsage
name|usage
init|=
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
decl_stmt|;
name|usage
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1024
operator|*
literal|150
argument_list|)
expr_stmt|;
name|String
name|body
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
operator|new
name|Queue
argument_list|(
name|brokerService
argument_list|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
argument_list|)
argument_list|,
literal|null
argument_list|,
operator|new
name|DestinationStatistics
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|underTest
operator|=
operator|new
name|FilePendingMessageCursor
argument_list|(
name|brokerService
operator|.
name|getBroker
argument_list|()
argument_list|,
literal|"test"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|setSystemUsage
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"start"
argument_list|)
expr_stmt|;
specifier|final
name|PageFile
name|pageFile
init|=
operator|(
operator|(
name|PListImpl
operator|)
name|underTest
operator|.
name|getDiskList
argument_list|()
operator|)
operator|.
name|getPageFile
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"page count: "
operator|+
name|pageFile
operator|.
name|getPageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"free count: "
operator|+
name|pageFile
operator|.
name|getFreePageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"content size: "
operator|+
name|pageFile
operator|.
name|getPageContentSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|initialPageCount
init|=
name|pageFile
operator|.
name|getPageCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numMessages
init|=
literal|1000
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
comment|// ensure free pages are reused
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMessages
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQMessage
name|mqMessage
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
name|mqMessage
operator|.
name|setStringProperty
argument_list|(
literal|"body"
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|mqMessage
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
literal|"1:2:3:"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|mqMessage
operator|.
name|setMemoryUsage
argument_list|(
name|usage
operator|.
name|getMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
name|mqMessage
operator|.
name|setRegionDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|addMessageLast
argument_list|(
operator|new
name|IndirectMessageReference
argument_list|(
name|mqMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"cursor is not full "
operator|+
name|usage
operator|.
name|getTempUsage
argument_list|()
argument_list|,
name|underTest
operator|.
name|isFull
argument_list|()
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|reset
argument_list|()
expr_stmt|;
name|long
name|receivedCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|underTest
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MessageReference
name|ref
init|=
name|underTest
operator|.
name|next
argument_list|()
decl_stmt|;
name|underTest
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id is correct"
argument_list|,
name|receivedCount
operator|++
argument_list|,
name|ref
operator|.
name|getMessageId
argument_list|()
operator|.
name|getProducerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"got all messages back"
argument_list|,
name|receivedCount
argument_list|,
name|numMessages
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"page count: "
operator|+
name|pageFile
operator|.
name|getPageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"free count: "
operator|+
name|pageFile
operator|.
name|getFreePageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"content size: "
operator|+
name|pageFile
operator|.
name|getPageContentSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"expected page usage"
argument_list|,
name|initialPageCount
argument_list|,
name|pageFile
operator|.
name|getPageCount
argument_list|()
operator|-
name|pageFile
operator|.
name|getFreePageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Destroy"
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"page count: "
operator|+
name|pageFile
operator|.
name|getPageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"free count: "
operator|+
name|pageFile
operator|.
name|getFreePageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"content size: "
operator|+
name|pageFile
operator|.
name|getPageContentSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"expected page usage"
argument_list|,
name|initialPageCount
operator|-
literal|1
argument_list|,
name|pageFile
operator|.
name|getPageCount
argument_list|()
operator|-
name|pageFile
operator|.
name|getFreePageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
