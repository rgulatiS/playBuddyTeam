import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, Button, StyleSheet, FlatList, Alert, TouchableOpacity, ScrollView, Image } from 'react-native';
import axios from 'axios';

const SearchCourtsScreen = ({ navigation }) => {
  const [selectedSportId, setSelectedSportId] = useState('');
  const [sports, setSports] = useState([]);
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [courts, setCourts] = useState([]);
  const [groupedData, setGroupedData] = useState([]);
  const [selectedCourtInGroups, setSelectedCourtInGroups] = useState({}); // { groupId: courtIndex }
  const [isLoading, setIsLoading] = useState(false);
  const [selectedSlot, setSelectedSlot] = useState(null); // { courtId, slot }

  useEffect(() => {
    fetchSports();
  }, []);

  const fetchSports = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/sports');
      setSports(response.data);
      if (response.data.length > 0) {
        setSelectedSportId(response.data[0].id.toString());
      }
    } catch (error) {
      console.error('Fetch sports error:', error);
    }
  };

  const groupCourts = (data) => {
    const groups = {};
    data.forEach(item => {
      if (!item.court || !item.court.venue) return;
      const groupId = `${item.court.venue.id}_${item.court.sport?.id || 'any'}`;
      if (!groups[groupId]) {
        groups[groupId] = {
          venue: item.court.venue,
          sport: item.court.sport,
          items: []
        };
      }
      groups[groupId].items.push(item);
    });
    return Object.values(groups);
  };

  const searchCourts = async () => {
    if (!selectedSportId || !date) {
      Alert.alert('Error', 'Please select a sport and date');
      return;
    }

    setIsLoading(true);
    setSelectedSlot(null);
    setSelectedCourtInGroups({});
    try {
      const token = localStorage.getItem('jwt_token');
      const response = await axios.get('http://localhost:8080/api/courts/available', {
        params: { sportId: selectedSportId, date: date },
        headers: { Authorization: `Bearer ${token}` }
      });
      console.log('Available Courts Response Data:', response.data);
      setCourts(response.data);
      const grouped = groupCourts(response.data);
      setGroupedData(grouped);

      if (response.data.length === 0) {
        Alert.alert('No Courts', 'No courts available for the selected sport and date.');
      }
    } catch (error) {
      console.error('Search error details:', error.response?.data || error.message);
      Alert.alert('Error', 'Failed to fetch courts. Ensure you are logged in.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSlotPress = (courtId, slot) => {
    setSelectedSlot({ courtId, slot });
  };

  return (
    <View style={styles.container}>
      <View style={styles.headerContainer}>
        <Text style={styles.header}>Find your Court</Text>
        <Text style={styles.subHeader}>Book the best sports venues near you</Text>
      </View>

      <View style={styles.filterSection}>
        <View style={styles.filterRow}>
          <View style={styles.filterItem}>
            <Text style={styles.label}>Sport</Text>
            <select
              style={webStyles.select}
              value={selectedSportId}
              onChange={(e) => setSelectedSportId(e.target.value)}
            >
              <option value="">Select Sport</option>
              {sports.map(s => (
                <option key={s.id} value={s.id}>{s.name}</option>
              ))}
            </select>
          </View>

          <View style={styles.filterItem}>
            <Text style={styles.label}>Date</Text>
            <TextInput
              style={styles.input}
              value={date}
              onChangeText={setDate}
              placeholder="YYYY-MM-DD"
            />
          </View>
        </View>

        <TouchableOpacity
          style={styles.searchButton}
          onPress={searchCourts}
          disabled={isLoading}
        >
          <Text style={styles.searchButtonText}>{isLoading ? "Searching..." : "Search Availability"}</Text>
        </TouchableOpacity>
      </View>

      {courts.length > 0 && (
        <Text style={styles.resultsHeader}>Available Spots ({courts.length})</Text>
      )}

      <ScrollView
        style={{ flex: 1, minHeight: 600 }}
        contentContainerStyle={styles.listContent}
      >
        {groupedData.length > 0 ? (
          groupedData.map((group, gIdx) => {
            const { venue, sport, items } = group;
            const groupId = `${venue.id}_${sport?.id || 'any'}`;
            const selectedIdx = selectedCourtInGroups[groupId] || 0;
            const currentItem = items[selectedIdx] || items[0];
            const { court, availableSlots } = currentItem;

            // Use domain-provided images (falling back to venue or sport defaults)
            let imageUrl = court.imageUrl || venue.imageUrl;
            if (!imageUrl) {
              const sportName = sport?.name?.toLowerCase() || '';
              if (sportName.includes('badminton')) imageUrl = '/images/badminton.png';
              else if (sportName.includes('tennis')) imageUrl = '/images/tennis.png';
              else if (sportName.includes('football')) imageUrl = '/images/football.png';
              else if (sportName.includes('cricket')) imageUrl = '/images/cricket.png';
            }

            return (
              <View key={groupId} style={styles.card}>
                <View style={styles.imageContainer}>
                  <Image
                    source={{ uri: imageUrl }}
                    style={styles.cardImage}
                    defaultSource={{ uri: 'https://via.placeholder.com/400x200?text=Sports+Arena' }}
                    onError={(e) => console.log('Image load error:', imageUrl)}
                  />
                </View>
                <View style={styles.cardBadge}>
                  <Text style={styles.badgeText}>{sport?.name}</Text>
                </View>

                <View style={styles.cardContent}>
                  <View style={styles.cardHeaderRow}>
                    <View style={{ flex: 1 }}>
                      <Text style={styles.venueName}>{venue.name || 'Grand Sports Arena'}</Text>
                      <Text style={styles.courtName}>{venue.address || 'Near Mumbai Highway'}</Text>
                    </View>
                    <View style={styles.priceContainer}>
                      <Text style={styles.price}>₹{court.pricePerHour}</Text>
                      <Text style={styles.priceUnit}>/hr</Text>
                    </View>
                  </View>

                  {/* Court Selector - Only show if more than one court */}
                  {items.length > 1 && (
                    <View style={{ marginBottom: 16 }}>
                      <Text style={styles.slotLabel}>Select Court</Text>
                      <View style={styles.slotsContainer}>
                        {items.map((it, idx) => (
                          <TouchableOpacity
                            key={it.court.id}
                            style={[
                              styles.courtChip,
                              selectedIdx === idx && styles.slotChipSelected
                            ]}
                            onPress={() => {
                              setSelectedCourtInGroups(prev => ({ ...prev, [groupId]: idx }));
                              setSelectedSlot(null); // Reset slot selection when switching courts
                            }}
                          >
                            <Text style={[
                              styles.slotText,
                              selectedIdx === idx && styles.slotTextSelected
                            ]}>
                              {it.court.name}
                            </Text>
                          </TouchableOpacity>
                        ))}
                      </View>
                    </View>
                  )}

                  <Text style={styles.slotLabel}>Available Slots</Text>
                  <View style={styles.slotsContainer}>
                    {availableSlots && availableSlots.length > 0 ? (
                      availableSlots.map((slot, sIdx) => {
                        const isSelected = selectedSlot?.courtId === court.id && selectedSlot?.slot === slot;
                        return (
                          <TouchableOpacity
                            key={sIdx}
                            style={[styles.slotChip, isSelected && styles.slotChipSelected]}
                            onPress={() => handleSlotPress(court.id, slot)}
                          >
                            <Text style={[styles.slotText, isSelected && styles.slotTextSelected]}>
                              {slot.substring(0, 5)}
                            </Text>
                          </TouchableOpacity>
                        );
                      })
                    ) : (
                      <Text style={{ color: '#999', fontSize: 13, marginBottom: 8 }}>No slots available</Text>
                    )}
                  </View>

                  <View style={styles.tagsRow}>
                    <View style={styles.tag}>
                      <Text style={styles.tagText}>👥 2-4 Players</Text>
                    </View>
                    <View style={[styles.tag, { backgroundColor: '#e8f5e9' }]}>
                      <Text style={[styles.tagText, { color: '#2e7d32' }]}>Instant Book</Text>
                    </View>
                  </View>

                  <TouchableOpacity
                    style={[
                      styles.bookButton,
                      { backgroundColor: (selectedSlot?.courtId === court.id) ? '#1A1A1A' : '#CCC' }
                    ]}
                    onPress={() => {
                      if (selectedSlot?.courtId === court.id) {
                        navigation.navigate('CourtBooking', {
                          court: court,
                          selectedSlot: selectedSlot.slot,
                          bookingDate: date
                        });
                      } else {
                        Alert.alert('Selection Required', 'Please select a time slot first.');
                      }
                    }}
                    disabled={selectedSlot?.courtId !== court.id}
                  >
                    <Text style={styles.bookButtonText}>Confirm & Book</Text>
                  </TouchableOpacity>
                </View>
              </View>
            );
          })
        ) : (
          !isLoading && courts.length > 0 && <Text style={styles.emptyText}>Processing results...</Text>
        )}
        {courts.length === 0 && !isLoading && (
          <Text style={styles.emptyText}>No courts found. Try another search!</Text>
        )}
      </ScrollView>
    </View>
  );
};

const webStyles = {
  select: {
    height: 48,
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 12,
    padding: '0 12px',
    fontSize: 16,
    backgroundColor: '#F5F5F5',
    width: '100%',
    appearance: 'none',
    outline: 'none',
  }
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8F9FB',
    display: 'flex',
    flexDirection: 'column',
  },
  headerContainer: {
    padding: 24,
    backgroundColor: '#fff',
  },
  header: {
    fontSize: 28,
    fontWeight: '800',
    color: '#1A1A1A',
    marginBottom: 4,
  },
  subHeader: {
    fontSize: 16,
    color: '#666',
  },
  filterSection: {
    padding: 24,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#F0F0F0',
    marginBottom: 12,
  },
  filterRow: {
    flexDirection: 'row',
    gap: 16,
    marginBottom: 20,
  },
  filterItem: {
    flex: 1,
  },
  label: {
    fontSize: 14,
    fontWeight: '700',
    color: '#444',
    marginBottom: 8,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  input: {
    height: 48,
    borderWidth: 1,
    borderColor: '#E0E0E0',
    borderRadius: 12,
    paddingHorizontal: 16,
    fontSize: 16,
    backgroundColor: '#F5F5F5',
  },
  searchButton: {
    backgroundColor: '#007AFF',
    height: 52,
    borderRadius: 14,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#007AFF',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 4,
  },
  searchButtonText: {
    color: '#fff',
    fontSize: 17,
    fontWeight: '700',
  },
  resultsHeader: {
    fontSize: 20,
    fontWeight: '700',
    color: '#333',
    paddingHorizontal: 24,
    marginBottom: 16,
    marginTop: 8,
  },
  listContent: {
    paddingHorizontal: 20,
    paddingBottom: 40,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 16,
    marginBottom: 24,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 12,
    elevation: 4,
    overflow: 'hidden',
    minHeight: 450,
    width: '100%',
    borderWidth: 1,
    borderColor: '#EEE',
  },
  imageContainer: {
    width: '100%',
    height: 200,
    position: 'relative',
    backgroundColor: '#F0F0F0',
  },
  cardImage: {
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
  },
  imagePlaceholder: {
    ...StyleSheet.absoluteFillObject,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#EEE',
  },
  placeholderText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#999',
  },
  cardBadge: {
    position: 'absolute',
    top: 16,
    left: 16,
    backgroundColor: 'rgba(0, 122, 255, 0.9)',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 8,
  },
  badgeText: {
    color: '#fff',
    fontSize: 12,
    fontWeight: '700',
  },
  cardContent: {
    padding: 16,
  },
  cardHeaderRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 8,
  },
  venueName: {
    fontSize: 18,
    fontWeight: '700',
    color: '#1A1A1A',
    marginBottom: 2,
  },
  courtName: {
    fontSize: 14,
    color: '#666',
  },
  priceContainer: {
    alignItems: 'flex-end',
  },
  price: {
    fontSize: 22,
    fontWeight: '800',
    color: '#007AFF',
  },
  priceUnit: {
    fontSize: 12,
    color: '#666',
    fontWeight: '600',
  },
  infoRow: {
    marginBottom: 16,
  },
  addressText: {
    fontSize: 14,
    color: '#888',
  },
  slotLabel: {
    fontSize: 13,
    fontWeight: '700',
    color: '#666',
    marginBottom: 8,
    textTransform: 'uppercase',
  },
  slotsContainer: {
    marginBottom: 16,
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  slotChip: {
    backgroundColor: '#fff',
    borderWidth: 1.5,
    borderColor: '#007AFF',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 8,
  },
  courtChip: {
    backgroundColor: '#fff',
    borderWidth: 1.5,
    borderColor: '#007AFF',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 8,
  },
  slotChipSelected: {
    backgroundColor: '#007AFF',
    borderColor: '#007AFF',
  },
  slotText: {
    fontSize: 14,
    color: '#007AFF',
    fontWeight: '700',
  },
  slotTextSelected: {
    color: '#fff',
  },
  tagsRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginBottom: 20,
  },
  tag: {
    backgroundColor: '#F0F2F5',
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 6,
  },
  tagText: {
    fontSize: 12,
    color: '#555',
    fontWeight: '600',
  },
  bookButton: {
    backgroundColor: '#1A1A1A',
    height: 48,
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
  },
  bookButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '700',
  },
  emptyText: {
    textAlign: 'center',
    marginTop: 40,
    color: '#999',
    fontSize: 16,
  }
});

export default SearchCourtsScreen;
