import { mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import StatisticsPanel from '../StatisticsPanel.vue';
import * as statisticsApi from '../../../api/statistics';

// Helper to wait for promises to resolve
const flushPromises = () => new Promise(setImmediate);

describe('StatisticsPanel', () => {
  let getFunnelCountsSpy: ReturnType<typeof vi.fn>;
  let getActivityOverTimeSpy: ReturnType<typeof vi.fn>;
  let getConversionRatesSpy: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    getFunnelCountsSpy = vi.spyOn(statisticsApi, 'getFunnelCounts');
    getActivityOverTimeSpy = vi.spyOn(statisticsApi, 'getActivityOverTime');
    getConversionRatesSpy = vi.spyOn(statisticsApi, 'getConversionRates');
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('displays loading state initially', async () => {
    // Mock API calls to take some time (not resolved immediately)
    getFunnelCountsSpy.mockReturnValue(new Promise(() => {}));
    getActivityOverTimeSpy.mockReturnValue(new Promise(() => {}));
    getConversionRatesSpy.mockReturnValue(new Promise(() => {}));

    const wrapper = mount(StatisticsPanel);
    expect(wrapper.find('.loading').exists()).toBe(true);
    expect(wrapper.find('.loading').text()).toBe('Loading statistics...');
  });

  it('displays funnel counts when data is loaded', async () => {
    const funnelData = [
      { stage: 'Applied', count: 5 },
      { stage: 'In progress', count: 3 },
      { stage: 'Interview', count: 2 },
      { stage: 'Offer', count: 1 },
      { stage: 'Rejected', count: 0 },
      { stage: 'Withdrawn', count: 0 }
    ];
    getFunnelCountsSpy.mockResolvedValue(funnelData);
    getActivityOverTimeSpy.mockResolvedValue([]);
    getConversionRatesSpy.mockResolvedValue([]);

    const wrapper = mount(StatisticsPanel);
    await flushPromises();
    await wrapper.vm.$nextTick();

    expect(wrapper.find('.loading').exists()).toBe(false);
    const funnelItems = wrapper.findAll('.funnel-item');
    expect(funnelItems).toHaveLength(6);
    expect(funnelItems.at(0).find('.stage-name').text()).toBe('Applied:');
    expect(funnelItems.at(0).find('.count').text()).toBe('5');
    expect(funnelItems.at(1).find('.count').text()).toBe('3');
    expect(funnelItems.at(2).find('.count').text()).toBe('2');
    expect(funnelItems.at(3).find('.count').text()).toBe('1');
    expect(funnelItems.at(4).find('.count').text()).toBe('0');
    expect(funnelItems.at(5).find('.count').text()).toBe('0');
  });

  it('displays activity over time when data is loaded', async () => {
    const activityData = [
      { date: '2026-08-20', count: 2 },
      { date: '2026-08-21', count: 1 },
      { date: '2026-08-22', count: 3 }
    ];
    getFunnelCountsSpy.mockResolvedValue([]);
    getActivityOverTimeSpy.mockResolvedValue(activityData);
    getConversionRatesSpy.mockResolvedValue([]);

    const wrapper = mount(StatisticsPanel);
    await flushPromises();
    await wrapper.vm.$nextTick();

    const activityItems = wrapper.findAll('.activity-item');
    expect(activityItems).toHaveLength(3);
    expect(activityItems.at(0).find('.date').text()).toBe('2026-08-20');
    expect(activityItems.at(0).find('.count').text()).toBe('2 applications');
    expect(activityItems.at(1).find('.date').text()).toBe('2026-08-21');
    expect(activityItems.at(1).find('.count').text()).toBe('1 application');
    expect(activityItems.at(2).find('.date').text()).toBe('2026-08-22');
    expect(activityItems.at(2).find('.count').text()).toBe('3 applications');
  });

  it('displays conversion rates when data is loaded', async () => {
    const conversionData = [
      { fromStage: 'Applied', toStage: 'In progress', rate: 0.5 },
      { fromStage: 'In progress', toStage: 'Interview', rate: 0.333 },
      { fromStage: 'Interview', toStage: 'Offer', rate: 0.2 },
      { fromStage: 'Offer', toStage: 'Rejected', rate: 0.1 },
      { fromStage: 'Rejected', toStage: 'Withdrawn', rate: 0.0 }
    ];
    getFunnelCountsSpy.mockResolvedValue([]);
    getActivityOverTimeSpy.mockResolvedValue([]);
    getConversionRatesSpy.mockResolvedValue(conversionData);

    const wrapper = mount(StatisticsPanel);
    await flushPromises();
    await wrapper.vm.$nextTick();

    const conversionItems = wrapper.findAll('.conversion-item');
    expect(conversionItems).toHaveLength(5);
    expect(conversionItems.at(0).find('.stage-pair').text()).toBe('Applied → In progress');
    expect(conversionItems.at(0).find('.rate').text()).toBe('50.0%');
    expect(conversionItems.at(1).find('.rate').text()).toBe('33.3%');
    expect(conversionItems.at(2).find('.rate').text()).toBe('20.0%');
    expect(conversionItems.at(3).find('.rate').text()).toBe('10.0%');
    expect(conversionItems.at(4).find('.rate').text()).toBe('0.0%');
  });

  it('shows error message when API call fails', async () => {
    getFunnelCountsSpy.mockRejectedValue(new Error('Network error'));
    getActivityOverTimeSpy.mockResolvedValue([]);
    getConversionRatesSpy.mockResolvedValue([]);

    const wrapper = mount(StatisticsPanel);
    await flushPromises();
    await wrapper.vm.$nextTick();

    expect(wrapper.find('.error').exists()).toBe(true);
    expect(wrapper.find('.error').text()).toContain('Failed to load statistics');
  });
});